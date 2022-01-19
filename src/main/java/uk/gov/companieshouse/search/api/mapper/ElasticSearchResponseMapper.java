package uk.gov.companieshouse.search.api.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.model.esdatamodel.PreviousCompanyName;

@Component
public class ElasticSearchResponseMapper {

    private static final String COMPANY_NAME_KEY = "company_name";
    private static final String CORPORATE_NAME_KEY = "corporate_name";
    private static final String COMPANY_NUMBER_KEY = "company_number";
    private static final String COMPANY_STATUS_KEY = "company_status";
    private static final String COMPANY_TYPE_KEY = "company_type";
    private static final String CURRENT_COMPANY_KEY = "current_company";
    private static final String SIC_CODES_KEY = "sic_codes";
    private static final String ITEMS_KEY = "items";
    private static final String LINKS_KEY = "links";
    private static final String SELF_KEY = "self";
    private static final String ORDERED_ALPHAKEY_WITH_ID_KEY = "ordered_alpha_key_with_id";
    private static final String REGISTERED_OFFICE_ADDRESS_KEY = "registered_office_address";
    private static final String ADDRESS_KEY = "address";
    private static final String ADDRESS_LINE_1 = "address_line_1";
    private static final String ADDRESS_LINE_2 = "address_line_2";
    private static final String POST_CODE_KEY = "post_code";
    private static final String POSTAL_CODE_KEY = "postal_code";
    private static final String LOCALITY_KEY = "locality";
    private static final String PREMISES_KEY = "premises";
    private static final String REGION_KEY = "region";
    private static final String COUNTRY_KEY = "country";
    private static final String DATE_OF_CESSATION = "date_of_cessation";
    private static final String DATE_OF_CREATION = "date_of_creation";
    private static final String PREVIOUS_COMPANY_NAMES_KEY = "previous_company_names";
    private static final String PREVIOUS_COMPANY_NAME_KEY = "name";
    private static final String CEASED_ON_KEY = "ceased_on";
    private static final String EFFECTIVE_FROM_KEY = "effective_from";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String SEARCH_RESULTS_KIND = "searchresults#dissolved-company";
    private static final String SEARCH_RESULTS_ALPHABETICAL_KIND = "searchresults#alphabetical-search";
    private static final String SEARCH_RESULTS_COMPANY_KIND = "search-results#company";
    
    private static final List<String> STATUS_LIST = new ArrayList<>();
    
    static {
        STATUS_LIST.add("dissolved");
        STATUS_LIST.add("closed");
        STATUS_LIST.add("converted-closed");
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);
    DateTimeFormatter advancedFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    public TopHit mapAlphabeticalTopHit(Company company) {
        TopHit topHit = new TopHit();

        topHit.setCompanyName(company.getCompanyName());
        topHit.setCompanyNumber(company.getCompanyNumber());
        topHit.setCompanyStatus(company.getCompanyStatus());
        topHit.setCompanyType(company.getCompanyType());
        topHit.setLinks(company.getLinks());
        topHit.setOrderedAlphaKeyWithId(company.getOrderedAlphaKeyWithId());
        topHit.setKind(SEARCH_RESULTS_ALPHABETICAL_KIND);

        return topHit;
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

    public TopHit mapAdvancedTopHit(Company advancedCompany) {
        TopHit topHit = new TopHit();

        topHit.setCompanyName(advancedCompany.getCompanyName());
        topHit.setCompanyNumber(advancedCompany.getCompanyNumber());
        topHit.setCompanyStatus(advancedCompany.getCompanyStatus());
        topHit.setCompanyType(advancedCompany.getCompanyType());
        topHit.setKind(advancedCompany.getKind());
        topHit.setRegisteredOfficeAddress(advancedCompany.getRegisteredOfficeAddress());
        topHit.setDateOfCessation(advancedCompany.getDateOfCessation());
        topHit.setDateOfCreation(advancedCompany.getDateOfCreation());
        topHit.setSicCodes(advancedCompany.getSicCodes());
        topHit.setLinks(advancedCompany.getLinks());

        return topHit;
    }

    public Company mapAlphabeticalResponse(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> items = (Map<String, Object>) sourceAsMap.get(ITEMS_KEY);
        Map<String, Object> links = (Map<String, Object>) sourceAsMap.get(LINKS_KEY);

        Company company = new Company();
        Links companyLinks = new Links();

        company.setCompanyName((String) (items.get(CORPORATE_NAME_KEY)));
        company.setCompanyNumber((String) (items.get(COMPANY_NUMBER_KEY)));
        company.setCompanyStatus((String) (items.get(COMPANY_STATUS_KEY)));
        company.setOrderedAlphaKeyWithId((String) sourceAsMap.get(ORDERED_ALPHA_KEY_WITH_ID));
        company.setKind(SEARCH_RESULTS_ALPHABETICAL_KIND);

        companyLinks.setCompanyProfile((String) (links.get(SELF_KEY)));
        company.setLinks(companyLinks);

        company.setCompanyType((String) sourceAsMap.get(COMPANY_TYPE_KEY));

        return company;
    }

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
        String companyStatus = (String) sourceAsMap.get(COMPANY_STATUS_KEY);
        dissolvedCompany.setCompanyStatus(companyStatus.toLowerCase());
        dissolvedCompany.setOrderedAlphaKeyWithId((String) sourceAsMap.get(ORDERED_ALPHAKEY_WITH_ID_KEY));
        dissolvedCompany.setKind(SEARCH_RESULTS_KIND);

        if (sourceAsMap.containsKey(DATE_OF_CESSATION)) {
            dissolvedCompany.setDateOfCessation(LocalDate.parse((String) sourceAsMap.get(DATE_OF_CESSATION), formatter));
        }

        if (sourceAsMap.containsKey(DATE_OF_CREATION)) {
            dissolvedCompany.setDateOfCreation(LocalDate.parse((String) sourceAsMap.get(DATE_OF_CREATION), formatter));
        }

        Address roAddress = null;
        if (addressToMap != null && isROABeforeOrEqualToTwentyYears(dissolvedCompany.getDateOfCessation())) {
            roAddress = mapRegisteredOfficeAddressFields(addressToMap);
        }

        dissolvedCompany.setRegisteredOfficeAddress(roAddress);

        return dissolvedCompany;
    }

    public Company mapAdvancedSearchResponse(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> currentCompanyMap = (Map<String, Object>) sourceAsMap.get(CURRENT_COMPANY_KEY);
        Map<String, Object> linksMap = (Map<String, Object>) sourceAsMap.get(LINKS_KEY);
        Map<String, Object> addressToMap = (Map<String, Object>) currentCompanyMap.get(ADDRESS_KEY);
        Company advancedCompany = new Company();

        advancedCompany.setCompanyName((String) currentCompanyMap.get(CORPORATE_NAME_KEY));
        advancedCompany.setCompanyNumber((String) currentCompanyMap.get(COMPANY_NUMBER_KEY));
        advancedCompany.setCompanyStatus((String) currentCompanyMap.get(COMPANY_STATUS_KEY));
        advancedCompany.setCompanyType((String) sourceAsMap.get(COMPANY_TYPE_KEY));
        advancedCompany.setKind(SEARCH_RESULTS_COMPANY_KIND);

        if (STATUS_LIST.contains(advancedCompany.getCompanyStatus().toLowerCase()) 
                && currentCompanyMap.containsKey(DATE_OF_CESSATION)) {
            advancedCompany.setDateOfCessation(LocalDate.parse((String) currentCompanyMap.get(DATE_OF_CESSATION), advancedFormatter));
        }

        if (currentCompanyMap.containsKey(DATE_OF_CREATION)) {
            advancedCompany.setDateOfCreation(LocalDate.parse((String) currentCompanyMap.get(DATE_OF_CREATION), advancedFormatter));
        }

        if (currentCompanyMap.containsKey(SIC_CODES_KEY)) {
            advancedCompany.setSicCodes((List<String>) currentCompanyMap.get(SIC_CODES_KEY));
        }

        Address roAddress = null;
        if (addressToMap != null) {
            roAddress = mapRegisteredOfficeAddressFields(addressToMap);
        }
        advancedCompany.setRegisteredOfficeAddress(roAddress);

        Links links = new Links();
        links.setCompanyProfile((String) (linksMap.get(SELF_KEY)));
        advancedCompany.setLinks(links);

        return advancedCompany;
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
            String companyStatus = (String) sourceAsMap.get(COMPANY_STATUS_KEY);
            dissolvedCompany.setCompanyStatus(companyStatus.toLowerCase());
            dissolvedCompany.setDateOfCessation((LocalDate.parse((String) sourceAsMap.get(DATE_OF_CESSATION), formatter)));
            dissolvedCompany.setDateOfCreation((LocalDate.parse((String) sourceAsMap.get(DATE_OF_CREATION), formatter)));
            dissolvedCompany.setKind(SEARCH_RESULTS_KIND);

            Map<String, Object> addressToMap = (Map<String, Object>) sourceAsMap.get(REGISTERED_OFFICE_ADDRESS_KEY);

            Address registeredOfficeAddress = null;
            if (addressToMap != null && isROABeforeOrEqualToTwentyYears(dissolvedCompany.getDateOfCessation())) {
                registeredOfficeAddress = mapRegisteredOfficeAddressFields(addressToMap);
            }

            if(previousCompanyNamesList != null) {
                dissolvedCompany.setPreviousCompanyNames(mapPreviousCompanyNames(previousCompanyNamesList));
            }

            PreviousCompanyName previousCompanyName = new PreviousCompanyName();
            previousCompanyName.setName((String) nameHit.getSourceAsMap().get(PREVIOUS_COMPANY_NAME_KEY));
            previousCompanyName.setDateOfNameCessation(
                    LocalDate.parse((String) nameHit.getSourceAsMap().get(CEASED_ON_KEY), formatter));
            previousCompanyName.setDateOfNameEffectiveness(
                    LocalDate.parse((String) nameHit.getSourceAsMap().get(EFFECTIVE_FROM_KEY), formatter));
            previousCompanyName.setCompanyNumber((String) sourceAsMap.get(COMPANY_NUMBER_KEY));

            dissolvedCompany.setMatchedPreviousCompanyName(previousCompanyName);

            dissolvedCompany.setRegisteredOfficeAddress(registeredOfficeAddress);
            results.add(dissolvedCompany);
        }
    }

    private Address mapRegisteredOfficeAddressFields(Map<String, Object> addressToMap) {
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

            if(addressToMap.containsKey(POST_CODE_KEY)) {
                registeredOfficeAddress.setPostalCode((String) addressToMap.get(POST_CODE_KEY));
            }

            if(addressToMap.containsKey(LOCALITY_KEY)) {
                registeredOfficeAddress.setLocality((String) addressToMap.get(LOCALITY_KEY));
            }

            if(addressToMap.containsKey(PREMISES_KEY)) {
                registeredOfficeAddress.setPremises((String) addressToMap.get(PREMISES_KEY));
            }

            if(addressToMap.containsKey(REGION_KEY)) {
                registeredOfficeAddress.setRegion((String) addressToMap.get(REGION_KEY));
            }

            if(addressToMap.containsKey(COUNTRY_KEY)) {
                registeredOfficeAddress.setCountry((String) addressToMap.get(COUNTRY_KEY));
            }

            return registeredOfficeAddress;
    }

    private List<PreviousCompanyName> mapPreviousCompanyNames(List<Object> previousCompanyNamesList) {
        List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
        for(Object o : previousCompanyNamesList){
            Map<String, Object> companyNames = (Map<String, Object>) o;
            PreviousCompanyName companyName = new PreviousCompanyName();
            companyName.setName((String) companyNames.get(PREVIOUS_COMPANY_NAME_KEY));
            companyName.setDateOfNameCessation(LocalDate.parse((String) companyNames.get(CEASED_ON_KEY),formatter));
            companyName.setDateOfNameEffectiveness(LocalDate.parse((String) companyNames.get(EFFECTIVE_FROM_KEY),formatter));
            companyName.setCompanyNumber((String) companyNames.get(COMPANY_NUMBER_KEY));
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
