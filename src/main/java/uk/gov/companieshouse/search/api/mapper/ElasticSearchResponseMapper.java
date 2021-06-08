package uk.gov.companieshouse.search.api.mapper;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.search.api.model.DissolvedTopHit;
import uk.gov.companieshouse.search.api.model.PreviousNamesTopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.previousnames.DissolvedPreviousName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ElasticSearchResponseMapper {

    private static final String SEARCH_RESULTS_KIND = "searchresults#dissolvedCompany";
    private static final String REGISTERED_OFFICE_ADDRESS_KEY = "registered_office_address";
    private static final String ADDRESS_LINE_1 = "address_line_1";
    private static final String ADDRESS_LINE_2 = "address_line_2";
    private static final String POSTAL_CODE_KEY = "post_code";
    private static final String DATE_OF_CESSATION = "date_of_cessation";
    private static final String DATE_OF_CREATION = "date_of_creation";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    public DissolvedCompany mapDissolvedResponse(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> address = (Map<String, Object>) sourceAsMap.get(REGISTERED_OFFICE_ADDRESS_KEY);
        List<Object> previousCompanyNamesList = (List<Object>) sourceAsMap.get("previous_company_names");
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

        Address roAddress = new Address();

        dissolvedCompany.setCompanyName((String) sourceAsMap.get("company_name"));
        dissolvedCompany.setCompanyNumber((String) sourceAsMap.get("company_number"));
        dissolvedCompany.setCompanyStatus((String) sourceAsMap.get("company_status"));
        dissolvedCompany.setOrderedAlphaKeyWithId((String) sourceAsMap.get("ordered_alpha_key_with_id"));
        dissolvedCompany.setKind(SEARCH_RESULTS_KIND);

        if (sourceAsMap.containsKey(DATE_OF_CESSATION)) {
            dissolvedCompany.setDateOfCessation(LocalDate.parse((String) sourceAsMap.get(DATE_OF_CESSATION), formatter));
        }

        if (sourceAsMap.containsKey(DATE_OF_CREATION)) {
            dissolvedCompany.setDateOfCreation(LocalDate.parse((String) sourceAsMap.get(DATE_OF_CREATION), formatter));
        }

        if(address != null && address.containsKey(ADDRESS_LINE_1)) {
            roAddress.setAddressLine1((String) address.get(ADDRESS_LINE_1));
        }

        if(address != null && address.containsKey(ADDRESS_LINE_2)) {
            roAddress.setAddressLine2((String) address.get(ADDRESS_LINE_2));
        }

        if(address != null && address.containsKey("locality")) {
            roAddress.setLocality((String) address.get("locality"));
        }

        if(address != null && address.containsKey(POSTAL_CODE_KEY)) {
            roAddress.setPostalCode((String) address.get(POSTAL_CODE_KEY));
        }

        dissolvedCompany.setAddress(roAddress);

        return dissolvedCompany;
    }

    public DissolvedTopHit mapDissolvedTopHit(DissolvedCompany dissolvedCompany) {
        DissolvedTopHit topHit = new DissolvedTopHit();

        topHit.setCompanyName(dissolvedCompany.getCompanyName());
        topHit.setCompanyNumber(dissolvedCompany.getCompanyNumber());
        topHit.setCompanyStatus(dissolvedCompany.getCompanyStatus());
        topHit.setOrderedAlphaKeyWithId(dissolvedCompany.getOrderedAlphaKeyWithId());
        topHit.setKind(dissolvedCompany.getKind());
        topHit.setAddress(dissolvedCompany.getAddress());
        topHit.setDateOfCessation(dissolvedCompany.getDateOfCessation());
        topHit.setDateOfCreation(dissolvedCompany.getDateOfCreation());

        if (dissolvedCompany.getPreviousCompanyNames() != null) {
            topHit.setPreviousCompanyNames(dissolvedCompany.getPreviousCompanyNames());
        }

        return topHit;
    }

    public PreviousNamesTopHit mapPreviousNamesTopHit(List<DissolvedPreviousName> results) {
        PreviousNamesTopHit topHit = new PreviousNamesTopHit();
        topHit.setPreviousCompanyName(results.get(0).getPreviousCompanyName());
        topHit.setCompanyName(results.get(0).getCompanyName());
        topHit.setCompanyNumber(results.get(0).getCompanyNumber());
        topHit.setKind(results.get(0).getKind());
        topHit.setAddress(results.get(0).getAddress());
        topHit.setDateOfCessation(results.get(0).getDateOfCessation());
        topHit.setDateOfCreation(results.get(0).getDateOfCreation());

        return topHit;
    }

    public List<DissolvedPreviousName> mapPreviousNames(SearchHits hits) {

        List<DissolvedPreviousName> results = new ArrayList<>();

        hits.forEach(h -> mapPreviousName(h, results));

        return results;
    }

    private void mapPreviousName(SearchHit hit, List<DissolvedPreviousName> results) {
        // company details at dissolution in the main hit
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        // previous name details in the inner hits
        Map<String, SearchHits> innerHits = hit.getInnerHits();

        // get the previous name details element from the inner hits
        SearchHits previousNames = innerHits.get("previous_company_names");
        
        for(SearchHit nameHit : previousNames.getHits()) {
            DissolvedPreviousName previousCompanyName = new DissolvedPreviousName();
            previousCompanyName.setCompanyName((String) sourceAsMap.get("company_name"));
            previousCompanyName.setCompanyNumber((String) sourceAsMap.get("company_number"));
            previousCompanyName.setDateOfCessation((LocalDate.parse((String) sourceAsMap.get(DATE_OF_CESSATION), formatter)));
            previousCompanyName.setDateOfCreation((LocalDate.parse((String) sourceAsMap.get(DATE_OF_CREATION), formatter)));
            previousCompanyName.setKind(SEARCH_RESULTS_KIND);

            Address roAddress = new Address();
            Map<String, Object> address = (Map<String, Object>) sourceAsMap.get(REGISTERED_OFFICE_ADDRESS_KEY);

            if(address != null && address.containsKey(POSTAL_CODE_KEY)) {
                roAddress.setPostalCode((String) address.get(POSTAL_CODE_KEY));
            } else {
                roAddress = null;
            }

            previousCompanyName.setAddress(roAddress);
            previousCompanyName.setPreviousCompanyName((String) nameHit.getSourceAsMap().get("name"));
            results.add(previousCompanyName);
        }
    }
}
