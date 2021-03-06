package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX_ALPHABETICAL;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.ORDERED_ALPHAKEY;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.ORDERED_ALPHAKEY_WITH_ID;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_AFTER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_BEFORE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SIZE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.createLoggingMap;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    @Autowired
    private AlphaKeyService alphaKeyService;

    @Autowired
    private AlphabeticalSearchRequests alphabeticalSearchRequests;

    @Autowired
    private ElasticSearchResponseMapper elasticSearchResponseMapper;

    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final int FALLBACK_QUERY_LIMIT = 25;
    private static final String TOP_LEVEL_ALPHABETICAL_KIND = "search#alphabetical-search";

    private Integer sizeAbove;
    private Integer sizeBelow;

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<Company> getAlphabeticalSearchResults(String corporateName, String searchBefore,
            String searchAfter, Integer size, String requestId) throws SearchException {
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
        String kind = TOP_LEVEL_ALPHABETICAL_KIND;

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

                Company company = elasticSearchResponseMapper.mapAlphabeticalResponse(topHit);
                topHitCompany = elasticSearchResponseMapper.mapAlphabeticalTopHit(company);

                if ((searchBefore == null && searchAfter == null) || (searchBefore != null && searchAfter != null)) {
                    results = prepareSearchResultsWithTopHit(size, requestId, logMap, topHitCompany, results,
                            orderedAlphakeyWithId, company);
                } else if (searchAfter != null) {
                    getLogger().info("Searching alphabetical companies after", logMap);
                    results.addAll(populateBelowResults(requestId, topHitCompany.getCompanyName(), searchAfter, size));
                } else {
                    getLogger().info("Searching alphabetical companies before", logMap);
                    results.addAll(populateAboveResults(requestId, topHitCompany.getCompanyName(), searchBefore, size));
                }
            }
        } catch (IOException e) {
            getLogger().error("failed to map highest map to company object", logMap);
            throw new SearchException("error occurred reading data for highest match from " + "searchHits", e);
        }
        return new SearchResults<>("", topHitCompany, results, kind);
    }

    private List<Company> prepareSearchResultsWithTopHit(Integer size, String requestId, Map<String, Object> logMap,
            TopHit topHitCompany, List<Company> results, String orderedAlphakeyWithId, Company company)
            throws IOException {
        checkSize(size);
        logMap.put(ORDERED_ALPHAKEY_WITH_ID, orderedAlphakeyWithId);
        getLogger().info("Default alphabetical search before and after tophit", logMap);
        if (sizeAbove > 0) {
            results = populateAboveResults(requestId, topHitCompany.getCompanyName(), orderedAlphakeyWithId,
                    sizeAbove);
        }
        results.add(company);
        if (sizeBelow > 0) {
            results.addAll(populateBelowResults(requestId, topHitCompany.getCompanyName(),
                    orderedAlphakeyWithId, sizeBelow));
        }
        return results;
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
     * 
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
        hits.forEach(h -> results.add(elasticSearchResponseMapper.mapAlphabeticalResponse(h)));
        return results;
    }

    /**
     * method to populate the entries before the ordered alphakey
     * 
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
        hits = alphabeticalSearchRequests.getAboveResultsResponse(requestId, orderedAlphakeyWithId, topHitCompanyName,
                size);
        hits.forEach(h -> results.add(elasticSearchResponseMapper.mapAlphabeticalResponse(h)));

        Collections.reverse(results);
        return results;
    }

    private String getOrderedAlphaKeyWithId(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return (String) sourceAsMap.get(ORDERED_ALPHA_KEY_WITH_ID);
    }

    private void checkSize(Integer size) {
        if ((size % 2) == 0) {
            sizeAbove = (size / 2);
            sizeBelow = (size / 2) - 1;
        } else {
            sizeAbove = Math.floorDiv(size, 2);
            sizeBelow = Math.floorDiv(size, 2);
        }
    }
}
