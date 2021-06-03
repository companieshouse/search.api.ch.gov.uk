package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX_ALPHABETICAL;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.ORDERED_ALPHAKEY;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_AFTER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_BEFORE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SIZE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.createLoggingMap;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

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
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
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
    public SearchResults<Company> getAlphabeticalSearchResults(String corporateName, String searchBefore, String searchAfter,
            Integer size, String requestId) throws SearchException {
        Map<String, Object> logMap = createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, corporateName);
        logMap.put(INDEX, INDEX_ALPHABETICAL);
        logIfNotNull(logMap, SEARCH_BEFORE, searchBefore);
        logIfNotNull(logMap, SEARCH_AFTER, searchAfter);
        logIfNotNull(logMap, SIZE, size);

        getLogger().info("Performing search request", logMap);
        logMap.remove(MESSAGE);

        String orderedAlphakey = "";
        TopHit topHitCompany = new TopHit();
        
        List<Company> results = new ArrayList<>();
        String kind = "search#alphabetical";

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(corporateName);
        if (alphaKeyResponse != null) {
            orderedAlphakey = alphaKeyResponse.getOrderedAlphaKey();
            logMap.put(ORDERED_ALPHAKEY, orderedAlphakey);
        }

        try {
            SearchHits hits = getSearchHits(orderedAlphakey, requestId);

            if (hits.getTotalHits().value == 0) {
                getLogger().info("A result was not found, reducing search term to find result", logMap);
                logMap.remove(MESSAGE);

                hits = peelbackSearchRequest(hits, orderedAlphakey, requestId);
            }

            if (hits.getTotalHits().value > 0) {
                getLogger().info("A result has been found", logMap);
                logMap.remove(MESSAGE);

                String orderedAlphakeyWithId;
                SearchHit topHit;
                orderedAlphakeyWithId = getOrderedAlphaKeyWithId(hits.getHits()[0]);
                topHit = hits.getHits()[0];

                Company company = getCompany(topHit);
                topHitCompany.setCompanyName(company.getCompanyName());
                topHitCompany.setCompanyNumber(company.getCompanyNumber());
                topHitCompany.setCompanyStatus(company.getCompanyStatus());
                topHitCompany.setOrderedAlphaKeyWithId(company.getOrderedAlphaKeyWithId());
                topHitCompany.setKind(company.getKind());

                if ((searchBefore == null && searchAfter == null) || (searchBefore != null && searchAfter != null)) {
                    getLogger().info("Default search before tophit and after top hit", logMap);
                    results = populateAboveResults(requestId, topHitCompany.getCompanyName(), orderedAlphakeyWithId, size);
                    results.add(company);
                    results.addAll(populateBelowResults(requestId, topHitCompany.getCompanyName(), orderedAlphakeyWithId, size));
                } else if(searchAfter != null){
                    getLogger().info("Searching only companies before ordered alpha key with id", logMap);
                    results.addAll(populateBelowResults(requestId, topHitCompany.getCompanyName(), searchAfter, size));
                } else {
                    getLogger().info("Searching only companies after ordered alpha key with id", logMap);
                    results.addAll(populateAboveResults(requestId, topHitCompany.getCompanyName(), searchBefore, size));
                }
            }
        } catch (IOException e) {
            getLogger().error("failed to map highest map to company object", logMap);
            throw new SearchException("error occurred reading data for highest match from " + "searchHits", e);
        }
        return new SearchResults<>("", topHitCompany, results, kind);
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
        Links companyLinks = new Links();

        company.setCompanyName((String) (items.get("corporate_name")));
        company.setCompanyNumber((String) (items.get("company_number")));
        company.setCompanyStatus((String) (items.get("company_status")));
        company.setOrderedAlphaKey((String) items.get("ordered_alpha_key"));
        company.setOrderedAlphaKeyWithId((String) sourceAsMap.get(ORDERED_ALPHA_KEY_WITH_ID));
        company.setKind((String) sourceAsMap.get("kind"));

        companyLinks.setSelf((String) (links.get("self")));
        company.setLinks(companyLinks);

        company.setCompanyType((String) sourceAsMap.get("company_type"));

        return company;
    }
}
