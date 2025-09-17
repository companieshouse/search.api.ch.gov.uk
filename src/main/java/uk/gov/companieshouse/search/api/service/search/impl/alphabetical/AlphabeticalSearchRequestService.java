package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.ORDERED_ALPHAKEY;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.ORDERED_ALPHAKEY_WITH_ID;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    private final AlphaKeyService alphaKeyService;
    private final AlphabeticalSearchRequests alphabeticalSearchRequests;
    private final ElasticSearchResponseMapper elasticSearchResponseMapper;
    private final EnvironmentReader environmentReader;
    private final ConfiguredIndexNamesProvider indices;

    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String TOP_LEVEL_ALPHABETICAL_KIND = "search#alphabetical-search";
    private static final String ALPHABETICAL_FALLBACK_QUERY_LIMIT = "ALPHABETICAL_FALLBACK_QUERY_LIMIT";

    private Integer sizeAbove;
    private Integer sizeBelow;

    public AlphabeticalSearchRequestService(AlphaKeyService alphaKeyService,
        AlphabeticalSearchRequests alphabeticalSearchRequests,
        ElasticSearchResponseMapper elasticSearchResponseMapper,
        EnvironmentReader environmentReader, ConfiguredIndexNamesProvider indices) {
        this.alphaKeyService = alphaKeyService;
        this.alphabeticalSearchRequests = alphabeticalSearchRequests;
        this.elasticSearchResponseMapper = elasticSearchResponseMapper;
        this.environmentReader = environmentReader;
        this.indices = indices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<Company> getAlphabeticalSearchResults(String corporateName, String searchBefore,
            String searchAfter, Integer size, String requestId) throws SearchException {
        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .companyName(corporateName)
                .indexName(indices.alphabetical())
                .searchBefore(searchBefore)
                .searchAfter(searchAfter)
                .size(String.valueOf(size))
                .build().getLogMap();

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
            if (hits.getTotalHits() != null && hits.getTotalHits().value == 0){
                getLogger().info("A result was not found, reducing search term to find result", logMap);
                logMap.remove(MESSAGE);

                hits = peelbackSearchRequest(hits, orderedAlphakey, requestId);
            }
            if (hits.getTotalHits() != null && hits.getTotalHits().value > 0) {
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

        Integer fallbackQueryLimit = environmentReader.getMandatoryInteger(ALPHABETICAL_FALLBACK_QUERY_LIMIT);

        for (int i = 0; i < orderedAlphakey.length(); i++) {
            if (hits.getTotalHits() != null && hits.getTotalHits().value > 0 || i == fallbackQueryLimit) {
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

        if (hits.getTotalHits() != null && hits.getTotalHits().value == 0) {
            hits = alphabeticalSearchRequests.getStartsWithResponse(orderedAlphakey, requestId);
        }

        if (hits.getTotalHits() != null && hits.getTotalHits().value == 0) {
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
