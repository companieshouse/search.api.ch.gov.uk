package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.elasticsearch.DissolvedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.DissolvedTopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DissolvedSearchRequestService {

    @Autowired
    private AlphaKeyService alphaKeyService;

    @Autowired
    private DissolvedSearchRequests dissolvedSearchRequests;

    private static final String DISSOLVED_SEARCH = "Dissolved Search: ";
    private static final String SEARCH_RESULTS_KIND = "searchresults#dissolvedCompany";

    public DissolvedSearchResults getSearchResults(String companyName, String requestId) throws SearchException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, companyName);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info("getting dissolved search results", logMap);

        String orderedAlphaKey = "";
        List<DissolvedCompany> results = new ArrayList<>();
        DissolvedTopHit topHit = new DissolvedTopHit();

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(companyName);
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
        }

        try {
            SearchHits hits = dissolvedSearchRequests.getBestMatchResponse(orderedAlphaKey, requestId);
            if (hits.getTotalHits().value == 0) {

                hits = dissolvedSearchRequests.getStartsWithResponse(orderedAlphaKey, requestId);
            }

            if (hits.getTotalHits().value == 0) {

                hits = dissolvedSearchRequests.getCorporateNameStartsWithResponse(orderedAlphaKey, requestId);
            }

            if (hits.getTotalHits().value > 0) {
                LoggingUtils.getLogger().info("A result has been found", logMap);

                String orderedAlphaKeyWithId = SearchRequestUtils.getOrderedAlphaKeyWithId(hits.getHits()[0]);
                SearchHit bestMatch = hits.getHits()[0];
                DissolvedCompany topHitCompany = mapESResponse(bestMatch);

                mapTopHit(topHit, topHitCompany);

                populateSearchResults(requestId, topHit.getCompanyName(), results, topHitCompany,
                        orderedAlphaKeyWithId);
            }
        } catch (IOException e) {
            LoggingUtils.getLogger().error("failed to map highest map to company object",
                    logMap);
            throw new SearchException("error occurred reading data for highest match from " + "searchHits", e);
        }

        return new DissolvedSearchResults("", topHit, results);
    }
    
    public String getLastUpdated(String requestId) {
        String results;
        try {
            results = dissolvedSearchRequests.getLastUpdated(requestId);
        } catch (IOException e) {
            // TODO don't do this! :) 
            results = "oops";
            e.printStackTrace();
        }
        return results;
    }

    private DissolvedCompany mapESResponse(SearchHit hit) {
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
                companyName.setDateOfNameCessation((String) companyNames.get("ceased_on"));
                companyName.setDateOfNameEffectiveness((String) companyNames.get("effective_from"));
                previousCompanyNames.add(companyName);
            }
            dissolvedCompany.setPreviousCompanyNames(previousCompanyNames);
        }

        Address roAddress = new Address();

        dissolvedCompany.setCompanyName((String) sourceAsMap.get("company_name"));
        dissolvedCompany.setCompanyNumber((String) sourceAsMap.get("company_number"));
        dissolvedCompany.setCompanyStatus((String) sourceAsMap.get("company_status"));
        dissolvedCompany.setKind(SEARCH_RESULTS_KIND);
        dissolvedCompany.setDateOfCessation((String) sourceAsMap.get("date_of_cessation"));
        dissolvedCompany.setDateOfCreation((String) sourceAsMap.get("date_of_creation"));
        if(address != null && address.containsKey("locality")) {            
            roAddress.setLocality((String) address.get("locality"));
        }
        if(address != null && address.containsKey("postal_code")) {            
            roAddress.setPostalCode((String) address.get("postal_code"));
        }

        dissolvedCompany.setAddress(roAddress);

        return dissolvedCompany;
    }

    private void mapTopHit(DissolvedTopHit topHit, DissolvedCompany dissolvedCompany) {
        topHit.setCompanyName(dissolvedCompany.getCompanyName());
        topHit.setCompanyNumber(dissolvedCompany.getCompanyNumber());
        topHit.setCompanyStatus(dissolvedCompany.getCompanyStatus());
        topHit.setKind(dissolvedCompany.getKind());
        topHit.setAddress(dissolvedCompany.getAddress());
        topHit.setDateOfCessation(dissolvedCompany.getDateOfCessation());
        topHit.setDateOfCreation(dissolvedCompany.getDateOfCreation());
        topHit.setPreviousCompanyNames(dissolvedCompany.getPreviousCompanyNames());
    }

    private void populateSearchResults(String requestId, 
            String topHitCompanyName, 
            List<DissolvedCompany> results,
            DissolvedCompany topHitCompany, 
            String orderedAlphaKeyWithId) throws IOException {
        SearchHits hits;
        hits = dissolvedSearchRequests.getAboveResultsResponse(requestId, orderedAlphaKeyWithId, topHitCompanyName);
        hits.forEach(h -> results.add(mapESResponse(h)));

        Collections.reverse(results);

        LoggingUtils.getLogger().info("Retrieving the top hit: " + topHitCompanyName);
        results.add(topHitCompany);

        hits = dissolvedSearchRequests.getDescendingResultsResponse(requestId, orderedAlphaKeyWithId,
                topHitCompanyName);

        hits.forEach(h -> results.add(mapESResponse(h)));
    }
}
