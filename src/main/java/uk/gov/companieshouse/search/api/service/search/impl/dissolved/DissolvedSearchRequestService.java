package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.ORDERED_ALPHAKEY_WITH_ID;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.elasticsearch.DissolvedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class DissolvedSearchRequestService {

    private final AlphaKeyService alphaKeyService;
    private final DissolvedSearchRequests dissolvedSearchRequests;
    private final ElasticSearchResponseMapper elasticSearchResponseMapper;
    private final EnvironmentReader environmentReader;
    private final ConfiguredIndexNamesProvider indices;

    private static final String TOP_KIND = "search#alphabetical-dissolved";
    private static final String RESULT_FOUND = "A result has been found";
    private static final String SEARCH_HITS = "searchHits";
    private static final String DISSOLVED_ALPHABETICAL_FALLBACK_QUERY_LIMIT = "DISSOLVED_ALPHABETICAL_FALLBACK_QUERY_LIMIT";
    
    private Integer sizeAbove;
    private Integer sizeBelow;

    public DissolvedSearchRequestService(AlphaKeyService alphaKeyService,
        DissolvedSearchRequests dissolvedSearchRequests,
        ElasticSearchResponseMapper elasticSearchResponseMapper,
        EnvironmentReader environmentReader, ConfiguredIndexNamesProvider indices) {
        this.alphaKeyService = alphaKeyService;
        this.dissolvedSearchRequests = dissolvedSearchRequests;
        this.elasticSearchResponseMapper = elasticSearchResponseMapper;
        this.environmentReader = environmentReader;
        this.indices = indices;
    }

    public SearchResults<Company> getSearchResults(String companyName, String searchBefore, String searchAfter,
                                                   Integer size, String requestId) throws SearchException {
        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .companyName(companyName)
                .indexName(indices.dissolved())
                .searchBefore(searchBefore)
                .searchAfter(searchAfter)
                .size(String.valueOf(size))
                .build().getLogMap();
        getLogger().info("getting dissolved search results", logMap);
        logMap.remove(MESSAGE);

        String orderedAlphaKey = "";
        List<Company> results = new ArrayList<>();
        TopHit topHit = new TopHit();
        String etag = GenerateEtagUtil.generateEtag();
        String kind = TOP_KIND;

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(companyName);
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
        }

        try {
            SearchHits hits = getSearchHits(orderedAlphaKey, requestId);

            if (hits.getTotalHits() != null && hits.getTotalHits().value == 0) {
                getLogger().info("A result was not found, reducing search term to find result", logMap);

                hits = peelbackSearchRequest(hits, orderedAlphaKey, requestId);
            }

            if (hits.getTotalHits() != null && hits.getTotalHits().value > 0) {
                getLogger().info(RESULT_FOUND, logMap);

                String orderedAlphaKeyWithId;
                SearchHit bestMatch;

                orderedAlphaKeyWithId = SearchRequestUtils.getOrderedAlphaKeyWithId(hits.getHits()[0]);
                bestMatch = hits.getHits()[0];

                Company topHitCompany = elasticSearchResponseMapper.mapDissolvedResponse(bestMatch);

                topHit = elasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany);

                if ((searchBefore == null && searchAfter == null) || (searchBefore != null && searchAfter != null)) {
                    results = prepareSearchResultsWithTopHit(size, requestId, logMap, results, topHit,
                            orderedAlphaKeyWithId, topHitCompany);
                } else if (searchAfter != null) {
                    getLogger().info("Searching dissolved companies after", logMap);
                    results.addAll(populateBelowResults(requestId, topHit.getCompanyName(), searchAfter, size));
                } else {
                    getLogger().info("Searching dissolved companies before", logMap);
                    results.addAll(populateAboveResults(requestId, topHit.getCompanyName(), searchBefore, size));
                }
            }
        } catch (IOException e) {
            getLogger().error("failed to map highest map to company object", logMap);
            throw new SearchException("error occurred reading data for highest match from " + SEARCH_HITS, e);
        }

        return new SearchResults<>(etag, topHit, results, kind);
    }

    private List<Company> prepareSearchResultsWithTopHit(Integer size, String requestId,
            Map<String, Object> logMap, List<Company> results, TopHit topHit,
            String orderedAlphaKeyWithId, Company topHitCompany) throws IOException {
        checkSize(size);
        logMap.put(ORDERED_ALPHAKEY_WITH_ID, orderedAlphaKeyWithId);
        getLogger().info("Default dissolved search before and after tophit", logMap);
        if (sizeAbove > 0) {
            results = populateAboveResults(requestId, topHit.getCompanyName(), orderedAlphaKeyWithId, sizeAbove);
        }
        results.add(topHitCompany);
        if (sizeBelow > 0) {
            results.addAll(
                    populateBelowResults(requestId, topHit.getCompanyName(), orderedAlphaKeyWithId, sizeBelow));
        }
        return results;
    }

    public SearchResults<Company> getBestMatchSearchResults(String companyName,
                                                            String requestId,
                                                            String searchType,
                                                            Integer startIndex,
                                                            Integer size) throws SearchException {
        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .companyName(companyName)
                .indexName(indices.dissolved())
                .startIndex(String.valueOf(startIndex))
                .searchType(searchType)
                .size(String.valueOf(size))
                .build().getLogMap();
        getLogger().info("getting dissolved " + searchType + " search results", logMap);

        String etag = GenerateEtagUtil.generateEtag();
        TopHit topHit = new TopHit();
        List<Company> results = new ArrayList<>();
        String kind = "search#dissolved";
        long numberOfHits;

        try {
            SearchHits hits  = dissolvedSearchRequests.getDissolved(companyName, requestId, searchType, startIndex, size);
            numberOfHits = hits.getTotalHits().value;

            if (hits.getTotalHits().value > 0) {
                getLogger().info(RESULT_FOUND, logMap);

                Company topHitCompany = elasticSearchResponseMapper.mapDissolvedResponse(hits.getHits()[0]);

                topHit = elasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany);

                hits.forEach(h -> results.add(elasticSearchResponseMapper.mapDissolvedResponse(h)));
            }
        } catch (IOException e) {
            getLogger().error("failed to get best match for dissolved company", logMap);
            throw new SearchException("error occurred reading data for best match from " + SEARCH_HITS, e);
        }

        SearchResults<Company> dissolvedSearchResults =
                new SearchResults<>(etag, topHit, results, kind);
        dissolvedSearchResults.setHits(numberOfHits);

        return dissolvedSearchResults;
    }

    public SearchResults<Company> getPreviousNamesResults(String companyName,
                                                          String requestId,
                                                          String searchType,
                                                          Integer startIndex,
                                                          Integer size) throws SearchException {
        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .companyName(companyName)
                .indexName(indices.dissolved())
                .startIndex(String.valueOf(startIndex))
                .searchType(searchType)
                .size(String.valueOf(size))
                .build().getLogMap();
        getLogger().info("getting dissolved " + searchType + " search results", logMap);

        String etag = GenerateEtagUtil.generateEtag();
        TopHit topHit = new TopHit();
        List<Company> results = new ArrayList<>();
        List<Company> resizedResults = new ArrayList<>();
        String kind = "search#previous-name-dissolved";
        long numberOfHits;

        try {
            SearchHits hits  = dissolvedSearchRequests.getDissolved(companyName, requestId, searchType, startIndex, size);
            numberOfHits = hits.getTotalHits().value;

            if (hits.getTotalHits().value > 0) {
                getLogger().info(RESULT_FOUND, logMap);

                results = elasticSearchResponseMapper.mapPreviousNames(hits);
                topHit = elasticSearchResponseMapper.mapDissolvedTopHit(results.get(0));

                int finalSize = results.size() < size ? results.size() : size;
                resizedResults = results.subList(0, finalSize);
            }
        } catch (IOException e) {
            getLogger().error("failed to get previous names for dissolved company", logMap);
            throw new SearchException("error occurred reading data for previous names from " + SEARCH_HITS, e);
        }

        SearchResults<Company> dissolvedSearchResults =
                new SearchResults<>(etag, topHit, resizedResults, kind);
        dissolvedSearchResults.setHits(numberOfHits);

        return dissolvedSearchResults;
    }

    private SearchHits getSearchHits(String orderedAlphakey, String requestId) throws IOException {
        SearchHits hits = dissolvedSearchRequests.getBestMatchResponse(orderedAlphakey, requestId);

        if (hits.getTotalHits() != null &&  hits.getTotalHits().value == 0) {
            hits = dissolvedSearchRequests.getStartsWithResponse(orderedAlphakey, requestId);
        }

        if ( hits.getTotalHits() != null && hits.getTotalHits().value == 0) {
            hits = dissolvedSearchRequests.getCorporateNameStartsWithResponse(orderedAlphakey, requestId);
        }
        return hits;
    }

    public SearchHits peelbackSearchRequest(SearchHits hits, String orderedAlphaKey, String requestId)
            throws IOException {

        Integer fallbackQueryLimit = environmentReader.getMandatoryInteger(DISSOLVED_ALPHABETICAL_FALLBACK_QUERY_LIMIT);

        for (int i = 0; i < orderedAlphaKey.length(); i++) {

            TotalHits totalHits = hits.getTotalHits();
            if ((totalHits != null && totalHits.value > 0) || i == fallbackQueryLimit) {
                return hits;
            }

            if (i != orderedAlphaKey.length() - 1) {
                String resultString = orderedAlphaKey.substring(0, orderedAlphaKey.length() - i);
                hits = getSearchHits(resultString, requestId);
            }
        }
        return hits;
    }

    private List<Company> populateBelowResults(String requestId, String topHitCompanyName,
            String orderedAlphaKeyWithId, Integer size) throws IOException {
        List<Company> results = new ArrayList<>();
        SearchHits hits;
        hits = dissolvedSearchRequests.getDescendingResultsResponse(requestId, orderedAlphaKeyWithId, topHitCompanyName,
                size);
        hits.forEach(h -> results.add(elasticSearchResponseMapper.mapDissolvedResponse(h)));
        return results;
    }

    private List<Company> populateAboveResults(String requestId, String topHitCompanyName,
            String orderedAlphaKeyWithId, Integer size) throws IOException {
        List<Company> results = new ArrayList<>();
        SearchHits hits;
        hits = dissolvedSearchRequests.getAboveResultsResponse(requestId, orderedAlphaKeyWithId, topHitCompanyName,
                size);
        hits.forEach(h -> results.add(elasticSearchResponseMapper.mapDissolvedResponse(h)));

        Collections.reverse(results);
        return results;
    }
    
    private void checkSize(Integer size) {
        if((size % 2) == 0) {
            sizeAbove = (size / 2);
            sizeBelow = (size / 2) -1;
        }else {
            sizeAbove = Math.floorDiv(size, 2);
            sizeBelow = Math.floorDiv(size, 2);         
        }
    }
}
