package uk.gov.companieshouse.search.api.mapper;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.DissolvedTopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;
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

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    public DissolvedCompany mapDissolvedResponse(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> address = (Map<String, Object>) sourceAsMap.get("address");
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
        dissolvedCompany.setKind(SEARCH_RESULTS_KIND);
        dissolvedCompany.setDateOfCessation(LocalDate.parse((String) sourceAsMap.get("date_of_cessation"), formatter));
        dissolvedCompany.setDateOfCreation(LocalDate.parse((String) sourceAsMap.get("date_of_creation"), formatter));
        if(address != null && address.containsKey("locality")) {
            roAddress.setLocality((String) address.get("locality"));
        }
        if(address != null && address.containsKey("postal_code")) {
            roAddress.setPostalCode((String) address.get("postal_code"));
        }

        dissolvedCompany.setAddress(roAddress);

        return dissolvedCompany;
    }

    public void mapDissolvedTopHit(DissolvedTopHit topHit, DissolvedCompany dissolvedCompany) {
        topHit.setCompanyName(dissolvedCompany.getCompanyName());
        topHit.setCompanyNumber(dissolvedCompany.getCompanyNumber());
        topHit.setCompanyStatus(dissolvedCompany.getCompanyStatus());
        topHit.setKind(dissolvedCompany.getKind());
        topHit.setAddress(dissolvedCompany.getAddress());
        topHit.setDateOfCessation(dissolvedCompany.getDateOfCessation());
        topHit.setDateOfCreation(dissolvedCompany.getDateOfCreation());

        if (dissolvedCompany.getPreviousCompanyNames() != null) {
            topHit.setPreviousCompanyNames(dissolvedCompany.getPreviousCompanyNames());
        }
    }

    public List<DissolvedPreviousName> mapPreviousNames(SearchHits hits) {

        List<DissolvedPreviousName> results = new ArrayList<>();

        hits.forEach(h -> mapPreviousName(h, results));

        return results;
    }

    private void mapPreviousName(SearchHit hit, List<DissolvedPreviousName> results) {

        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        String companyName = (String) sourceAsMap.get("company_name");
        String companyNumber = (String) sourceAsMap.get("company_name");
        String companyStatus = (String) sourceAsMap.get("company_status");
        String kind = SEARCH_RESULTS_KIND;
        LocalDate dateOfCessation = (LocalDate.parse((String) sourceAsMap.get("date_of_cessation"), formatter));
        LocalDate dateOfCreation = (LocalDate.parse((String) sourceAsMap.get("date_of_creation"), formatter));

        Map<String, SearchHits> innerHits = hit.getInnerHits();

        innerHits.forEach((k, ih) -> ih.forEach(i -> System.out.println(i)));

    }

//    private void mapInnerHits() {
//
//    }
}
