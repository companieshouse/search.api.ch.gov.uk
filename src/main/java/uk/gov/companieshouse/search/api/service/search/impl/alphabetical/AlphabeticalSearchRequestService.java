package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    @Autowired
    private AlphaKeyService alphaKeyService;
    @Autowired
    private AlphabeticalSearchRequests alphabeticalSearchRequests;

    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final int FALLBACK_QUERY_LIMIT = 25;

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults getAlphabeticalSearchResults(String corporateName, String searchBefore, String searchAfter,
            Integer size, String requestId) throws SearchException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, corporateName);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_ALPHABETICAL);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SEARCH_BEFORE, searchBefore);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SEARCH_AFTER, searchAfter);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SIZE, size);

        LoggingUtils.getLogger().info("Performing search request", logMap);
        logMap.remove(LoggingUtils.MESSAGE);

        String orderedAlphakey = "";
        String topHitCompanyName = "";
        List<Company> results = new ArrayList<>();

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(corporateName);
        if (alphaKeyResponse != null) {
            orderedAlphakey = alphaKeyResponse.getOrderedAlphaKey();
            logMap.put(LoggingUtils.ORDERED_ALPHAKEY, orderedAlphakey);
        }

        try {
            SearchHits hits = getSearchHits(orderedAlphakey, requestId);

            if (hits.getTotalHits().value == 0) {
                LoggingUtils.getLogger().info("A result was not found, reducing search term to find result", logMap);
                logMap.remove(LoggingUtils.MESSAGE);

                hits = peelbackSearchRequest(hits, orderedAlphakey, requestId);
            }

            if (hits.getTotalHits().value > 0) {
                LoggingUtils.getLogger().info("A result has been found", logMap);
                logMap.remove(LoggingUtils.MESSAGE);

                String orderedAlphakeyWithId;
                SearchHit topHit;
                orderedAlphakeyWithId = getOrderedAlphaKeyWithId(hits.getHits()[0]);
                topHit = hits.getHits()[0];

                Company topHitCompany = getCompany(topHit);
                topHitCompanyName = topHitCompany.getItems().getCorporateName();

                if ((searchBefore == null && searchAfter == null) || (searchBefore != null && searchAfter != null)) {
                    results = populateAboveResults(requestId, topHitCompanyName, orderedAlphakeyWithId, size);
                    results.add(topHitCompany);
                    results.addAll(populateBelowResults(requestId, topHitCompanyName, orderedAlphakeyWithId, size));
                } else if(searchAfter != null){
                    results.addAll(populateBelowResults(requestId, topHitCompanyName, searchAfter, size));
                } else {
                    results.addAll(populateAboveResults(requestId, topHitCompanyName, searchBefore, size));
                }
            }
        } catch (IOException e) {
            LoggingUtils.getLogger().error("failed to map highest map to company object", logMap);
            throw new SearchException("error occurred reading data for highest match from " + "searchHits", e);
        }
        return new SearchResults("", topHitCompanyName, results);
    }

    public SearchHits peelbackSearchRequest(SearchHits hits, String orderedAlphakey, String requestId)
            throws IOException {
        for (int i = 0; i < orderedAlphakey.length(); i++) {

            if (hits.getTotalHits().value > 0 || i == FALLBACK_QUERY_LIMIT) {
                return hits;
            }

            if (i != orderedAlphakey.length() - 1) {
                String resultString = orderedAlphakey.substring(0, orderedAlphakey.length() - i);
                hits = getSearchHits(resultString, requestId);
            }
        }
        return hits;
    }

    private SearchHits getSearchHits(String orderedAlphakey, String requestId) throws IOException {
        SearchHits hits = alphabeticalSearchRequests.getBestMatchResponse(orderedAlphakey, requestId);

        if (hits.getTotalHits().value == 0) {
            hits = alphabeticalSearchRequests.getStartsWithResponse(orderedAlphakey, requestId);
        }

        if (hits.getTotalHits().value == 0) {
            hits = alphabeticalSearchRequests.getCorporateNameStartsWithResponse(orderedAlphakey, requestId);
        }
        return hits;
    }

    /**
     * method to populate the entries following the ordered alphakey
     * @param requestId
     * @param topHitCompanyName
     * @param orderedAlphakeyWithId
     * @param size
     * @return the list of company objects returned from ES
     * @throws IOException
     */
    private List<Company> populateBelowResults(String requestId, String topHitCompanyName, String orderedAlphakeyWithId,
            Integer size) throws IOException {
        List<Company> results = new ArrayList<>();
        SearchHits hits;
        hits = alphabeticalSearchRequests.getDescendingResultsResponse(requestId, orderedAlphakeyWithId,
                topHitCompanyName, size);
        hits.forEach(h -> results.add(getCompany(h)));
        return results;
    }

    /**
     * method to populate the entries before the ordered alphakey
     * @param requestId
     * @param topHitCompanyName
     * @param orderedAlphakeyWithId
     * @param size
     * @return the list of company objects returned from ES
     * @throws IOException
     */
    private List<Company> populateAboveResults(String requestId, String topHitCompanyName, String orderedAlphakeyWithId,
            Integer size) throws IOException {
        List<Company> results = new ArrayList<>();
        SearchHits hits;
        hits = alphabeticalSearchRequests.getAboveResultsResponse(requestId, orderedAlphakeyWithId, topHitCompanyName, size);
        hits.forEach(h -> results.add(getCompany(h)));

        Collections.reverse(results);
        return results;
    }

    private String getOrderedAlphaKeyWithId(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return (String) sourceAsMap.get(ORDERED_ALPHA_KEY_WITH_ID);
    }

    private Company getCompany(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> items = (Map<String, Object>) sourceAsMap.get("items");
        Map<String, Object> links = (Map<String, Object>) sourceAsMap.get("links");

        Company company = new Company();
        Items companyItems = new Items();
        Links companyLinks = new Links();

        companyItems.setCorporateName((String) (items.get("corporate_name")));
        companyItems.setCompanyNumber((String) (items.get("company_number")));
        companyItems.setCompanyStatus((String) (items.get("company_status")));
        companyItems.setOrderedAlphaKey((String) items.get("ordered_alpha_key"));
        companyItems.setOrderedAlphaKeyWithId((String) sourceAsMap.get(ORDERED_ALPHA_KEY_WITH_ID));

        companyLinks.setSelf((String) (links.get("self")));

        company.setId((String) sourceAsMap.get("ID"));
        company.setCompanyType((String) sourceAsMap.get("company_type"));
        company.setItems(companyItems);
        company.setLinks(companyLinks);

        return company;
    }
}
