package uk.gov.companieshouse.search.api.mapper;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.DissolvedTopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.PreviousCompanyName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ElasticSearchResponseMapper {

    private static final String SEARCH_RESULTS_KIND = "searchresults#dissolvedCompany";
    private static final String COMPANY_NAME_KEY = "company_name";
    private static final String COMPANY_NUMBER_KEY = "company_number";
    private static final String COMPANY_STATUS_KEY = "company_status";
    private static final String REGISTERED_OFFICE_ADDRESS_KEY = "registered_office_address";
    private static final String ADDRESS_LINE_1 = "address_line_1";
    private static final String ADDRESS_LINE_2 = "address_line_2";
    private static final String POSTAL_CODE_KEY = "post_code";
    private static final String LOCALITY_KEY = "locality";
    private static final String DATE_OF_CESSATION = "date_of_cessation";
    private static final String DATE_OF_CREATION = "date_of_creation";
    private static final String PREVIOUS_COMPANY_NAMES_KEY = "previous_company_names";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    public DissolvedCompany mapDissolvedResponse(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> addressToMap = (Map<String, Object>) sourceAsMap.get(REGISTERED_OFFICE_ADDRESS_KEY);
        List<Object> previousCompanyNamesList = (List<Object>) sourceAsMap.get(PREVIOUS_COMPANY_NAMES_KEY);
        DissolvedCompany dissolvedCompany = new DissolvedCompany();
        if(previousCompanyNamesList != null) {
            List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
            for(Object o : previousCompanyNamesList){
                Map<String, Object> companyNames = (Map<String, Object>) o;
                PreviousCompanyName companyName = new PreviousCompanyName();
                companyName.setName((String) companyNames.get("name"));
                companyName.setDateOfNameCessation(LocalDate.parse((String) companyNames.get("ceased_on"),formatter));
                companyName.setDateOfNameEffectiveness(LocalDate.parse((String) companyNames.get("effective_from"),formatter));
                previousCompanyNames.add(companyName);
            }
            dissolvedCompany.setPreviousCompanyNames(previousCompanyNames);
        }

        dissolvedCompany.setCompanyName((String) sourceAsMap.get(COMPANY_NAME_KEY));
        dissolvedCompany.setCompanyNumber((String) sourceAsMap.get(COMPANY_NUMBER_KEY));
        dissolvedCompany.setCompanyStatus((String) sourceAsMap.get(COMPANY_STATUS_KEY));
        dissolvedCompany.setOrderedAlphaKeyWithId((String) sourceAsMap.get("ordered_alpha_key_with_id"));
        dissolvedCompany.setKind(SEARCH_RESULTS_KIND);

        if (sourceAsMap.containsKey(DATE_OF_CESSATION)) {
            dissolvedCompany.setDateOfCessation(LocalDate.parse((String) sourceAsMap.get(DATE_OF_CESSATION), formatter));
        }

        if (sourceAsMap.containsKey(DATE_OF_CREATION)) {
            dissolvedCompany.setDateOfCreation(LocalDate.parse((String) sourceAsMap.get(DATE_OF_CREATION), formatter));
        }

        Address roAddress = setRegisteredOfficeAddressFields(addressToMap);
        dissolvedCompany.setRegisteredOfficeAddress(roAddress);

        return dissolvedCompany;
    }

    public DissolvedTopHit mapDissolvedTopHit(DissolvedCompany dissolvedCompany) {
        DissolvedTopHit topHit = new DissolvedTopHit();

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

        return topHit;
    }

    public DissolvedTopHit mapPreviousNamesTopHit(List<DissolvedCompany> results) {
        DissolvedTopHit topHit = new DissolvedTopHit();
        topHit.setCompanyName(results.get(0).getCompanyName());
        topHit.setCompanyNumber(results.get(0).getCompanyNumber());
        topHit.setKind(results.get(0).getKind());
        topHit.setRegisteredOfficeAddress(results.get(0).getRegisteredOfficeAddress());
        topHit.setDateOfCessation(results.get(0).getDateOfCessation());
        topHit.setDateOfCreation(results.get(0).getDateOfCreation());

        return topHit;
    }

    public List<DissolvedCompany> mapPreviousNames(SearchHits hits) {

        List<DissolvedCompany> results = new ArrayList<>();

        hits.forEach(h -> mapPreviousName(h, results));

        return results;
    }

    private void mapPreviousName(SearchHit hit, List<DissolvedCompany> results) {
        // company details at dissolution in the main hit
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        // previous name details in the inner hits
        Map<String, SearchHits> innerHits = hit.getInnerHits();

        // get the previous name details element from the inner hits
        SearchHits previousNames = innerHits.get(PREVIOUS_COMPANY_NAMES_KEY);
        
        for(SearchHit nameHit : previousNames.getHits()) {
            DissolvedCompany previousCompanyName = new DissolvedCompany();
            previousCompanyName.setCompanyName((String) sourceAsMap.get(COMPANY_NAME_KEY));
            previousCompanyName.setCompanyNumber((String) sourceAsMap.get(COMPANY_NUMBER_KEY));
            previousCompanyName.setDateOfCessation((LocalDate.parse((String) sourceAsMap.get(DATE_OF_CESSATION), formatter)));
            previousCompanyName.setDateOfCreation((LocalDate.parse((String) sourceAsMap.get(DATE_OF_CREATION), formatter)));
            previousCompanyName.setKind(SEARCH_RESULTS_KIND);

            Map<String, Object> addressToMap = (Map<String, Object>) sourceAsMap.get(REGISTERED_OFFICE_ADDRESS_KEY);
            Address registeredOfficeAddress = setRegisteredOfficeAddressFields(addressToMap);

            previousCompanyName.setRegisteredOfficeAddress(registeredOfficeAddress);
            results.add(previousCompanyName);
        }
    }

    private Address setRegisteredOfficeAddressFields(Map<String, Object> addressToMap) {
        if (addressToMap != null) {
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
        } else {
            return null;
        }
    }
}
