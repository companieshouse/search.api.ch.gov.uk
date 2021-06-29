package uk.gov.companieshouse.search.api.mapper;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.PreviousCompanyName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ElasticSearchResponseMapper {

    private static final String SEARCH_RESULTS_KIND = "searchresults#dissolved-company";
    private static final String COMPANY_NAME_KEY = "company_name";
    private static final String COMPANY_NUMBER_KEY = "company_number";
    private static final String COMPANY_STATUS_KEY = "company_status";
    private static final String ORDERED_ALPHAKEY_WITH_ID_KEY = "ordered_alpha_key_with_id";
    private static final String REGISTERED_OFFICE_ADDRESS_KEY = "registered_office_address";
    private static final String ADDRESS_LINE_1 = "address_line_1";
    private static final String ADDRESS_LINE_2 = "address_line_2";
    private static final String POSTAL_CODE_KEY = "post_code";
    private static final String LOCALITY_KEY = "locality";
    private static final String DATE_OF_CESSATION = "date_of_cessation";
    private static final String DATE_OF_CREATION = "date_of_creation";
    private static final String PREVIOUS_COMPANY_NAMES_KEY = "previous_company_names";
    private static final String PREVIOUS_COMPANY_NAME_KEY = "name";
    private static final String CEASED_ON_KEY = "ceased_on";
    private static final String EFFECTIVE_FROM_KEY = "effective_from";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    public Company mapDissolvedResponse(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> addressToMap = (Map<String, Object>) sourceAsMap.get(REGISTERED_OFFICE_ADDRESS_KEY);
        List<Object> previousCompanyNamesList = (List<Object>) sourceAsMap.get(PREVIOUS_COMPANY_NAMES_KEY);
        Company dissolvedCompany = new Company();
        if(previousCompanyNamesList != null) {
            dissolvedCompany.setPreviousCompanyNames(mapPreviousCompanyNames(previousCompanyNamesList));
        }

        dissolvedCompany.setCompanyName((String) sourceAsMap.get(COMPANY_NAME_KEY));
        dissolvedCompany.setCompanyNumber((String) sourceAsMap.get(COMPANY_NUMBER_KEY));
        dissolvedCompany.setCompanyStatus((String) sourceAsMap.get(COMPANY_STATUS_KEY));
        dissolvedCompany.setOrderedAlphaKeyWithId((String) sourceAsMap.get(ORDERED_ALPHAKEY_WITH_ID_KEY));
        dissolvedCompany.setKind(SEARCH_RESULTS_KIND);

        if (sourceAsMap.containsKey(DATE_OF_CESSATION)) {
            dissolvedCompany.setDateOfCessation(LocalDate.parse((String) sourceAsMap.get(DATE_OF_CESSATION), formatter));
        }

        if (sourceAsMap.containsKey(DATE_OF_CREATION)) {
            dissolvedCompany.setDateOfCreation(LocalDate.parse((String) sourceAsMap.get(DATE_OF_CREATION), formatter));
        }

        Address roAddress = mapRegisteredOfficeAddressFields(addressToMap, dissolvedCompany.getDateOfCessation());
        dissolvedCompany.setRegisteredOfficeAddress(roAddress);

        return dissolvedCompany;
    }

    public TopHit mapDissolvedTopHit(Company dissolvedCompany) {
        TopHit topHit = new TopHit();

        topHit.setCompanyName(dissolvedCompany.getCompanyName());
        topHit.setCompanyNumber(dissolvedCompany.getCompanyNumber());
        topHit.setCompanyStatus(dissolvedCompany.getCompanyStatus());
        topHit.setOrderedAlphaKeyWithId(dissolvedCompany.getOrderedAlphaKeyWithId());
        topHit.setKind(dissolvedCompany.getKind());
        topHit.setRegisteredOfficeAddress(dissolvedCompany.getRegisteredOfficeAddress());
        topHit.setDateOfCessation(dissolvedCompany.getDateOfCessation());
        topHit.setDateOfCreation(dissolvedCompany.getDateOfCreation());

        if (dissolvedCompany.getPreviousCompanyNames() != null) {
            topHit.setPreviousCompanyNames(dissolvedCompany.getPreviousCompanyNames());
        }

        if (dissolvedCompany.getMatchedPreviousCompanyName() != null) {
            topHit.setMatchedPreviousCompanyName(dissolvedCompany.getMatchedPreviousCompanyName());
        }

        return topHit;
    }

    public List<Company> mapPreviousNames(SearchHits hits) {

        List<Company> results = new ArrayList<>();

        hits.forEach(h -> mapPreviousName(h, results));

        return results;
    }

    private void mapPreviousName(SearchHit hit, List<Company> results) {
        // company details at dissolution in the main hit
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();

        // companies full list of previous names
        List<Object> previousCompanyNamesList = (List<Object>) sourceAsMap.get(PREVIOUS_COMPANY_NAMES_KEY);

        // previous name match details in the inner hits
        Map<String, SearchHits> innerHits = hit.getInnerHits();

        // get the previous name details element from the inner hits
        SearchHits previousNames = innerHits.get(PREVIOUS_COMPANY_NAMES_KEY);
        
        for(SearchHit nameHit : previousNames.getHits()) {
            Company dissolvedCompany = new Company();
            dissolvedCompany.setCompanyName((String) sourceAsMap.get(COMPANY_NAME_KEY));
            dissolvedCompany.setCompanyNumber((String) sourceAsMap.get(COMPANY_NUMBER_KEY));
            dissolvedCompany.setCompanyStatus((String) sourceAsMap.get(COMPANY_STATUS_KEY));
            dissolvedCompany.setDateOfCessation((LocalDate.parse((String) sourceAsMap.get(DATE_OF_CESSATION), formatter)));
            dissolvedCompany.setDateOfCreation((LocalDate.parse((String) sourceAsMap.get(DATE_OF_CREATION), formatter)));
            dissolvedCompany.setKind(SEARCH_RESULTS_KIND);

            Map<String, Object> addressToMap = (Map<String, Object>) sourceAsMap.get(REGISTERED_OFFICE_ADDRESS_KEY);
            Address registeredOfficeAddress = mapRegisteredOfficeAddressFields(addressToMap, dissolvedCompany.getDateOfCessation());

            if(previousCompanyNamesList != null) {
                dissolvedCompany.setPreviousCompanyNames(mapPreviousCompanyNames(previousCompanyNamesList));
            }

            PreviousCompanyName previousCompanyName = new PreviousCompanyName();
            previousCompanyName.setName((String) nameHit.getSourceAsMap().get(PREVIOUS_COMPANY_NAME_KEY));
            previousCompanyName.setDateOfNameCessation(
                    LocalDate.parse((String) nameHit.getSourceAsMap().get(CEASED_ON_KEY), formatter));
            previousCompanyName.setDateOfNameEffectiveness(
                    LocalDate.parse((String) nameHit.getSourceAsMap().get(EFFECTIVE_FROM_KEY), formatter));

            dissolvedCompany.setMatchedPreviousCompanyName(previousCompanyName);

            dissolvedCompany.setRegisteredOfficeAddress(registeredOfficeAddress);
            results.add(dissolvedCompany);
        }
    }

    private Address mapRegisteredOfficeAddressFields(Map<String, Object> addressToMap, LocalDate dateOfCessation) {
        if (addressToMap != null && isROABeforeOrEqualToTwentyYears(dateOfCessation)) {
            Address registeredOfficeAddress = new Address();

            if(addressToMap.containsKey(ADDRESS_LINE_1)) {
                registeredOfficeAddress.setAddressLine1((String) addressToMap.get(ADDRESS_LINE_1));
            }

            if(addressToMap.containsKey(ADDRESS_LINE_2)) {
                registeredOfficeAddress.setAddressLine2((String) addressToMap.get(ADDRESS_LINE_2));
            }

            if(addressToMap.containsKey(POSTAL_CODE_KEY)) {
                registeredOfficeAddress.setPostalCode((String) addressToMap.get(POSTAL_CODE_KEY));
            }

            if(addressToMap.containsKey(LOCALITY_KEY)) {
                registeredOfficeAddress.setLocality((String) addressToMap.get(LOCALITY_KEY));
            }

            return registeredOfficeAddress;
        }
        return null;
    }

    private List<PreviousCompanyName> mapPreviousCompanyNames(List<Object> previousCompanyNamesList) {
        List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
        for(Object o : previousCompanyNamesList){
            Map<String, Object> companyNames = (Map<String, Object>) o;
            PreviousCompanyName companyName = new PreviousCompanyName();
            companyName.setName((String) companyNames.get(PREVIOUS_COMPANY_NAME_KEY));
            companyName.setDateOfNameCessation(LocalDate.parse((String) companyNames.get(CEASED_ON_KEY),formatter));
            companyName.setDateOfNameEffectiveness(LocalDate.parse((String) companyNames.get(EFFECTIVE_FROM_KEY),formatter));
            previousCompanyNames.add(companyName);
        }
        return previousCompanyNames;
    }

    private boolean isROABeforeOrEqualToTwentyYears(LocalDate dateOfCessation) {
        if (dateOfCessation != null) {
            LocalDate currentDate = LocalDate.now();
            LocalDate dateTwentyYearsInPast = LocalDate.from(currentDate.minusYears(20));

            return dateOfCessation.isAfter(dateTwentyYearsInPast);
        }
        return false;
    }
}
