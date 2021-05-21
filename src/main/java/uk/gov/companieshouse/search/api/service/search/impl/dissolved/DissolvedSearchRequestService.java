package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.search.api.elasticsearch.DissolvedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.DissolvedTopHit;
import uk.gov.companieshouse.search.api.model.PreviousNamesTopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.previousnames.DissolvedPreviousName;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;

@Service
public class DissolvedSearchRequestService {

    @Autowired
    private AlphaKeyService alphaKeyService;

    @Autowired
    private DissolvedSearchRequests dissolvedSearchRequests;

    @Autowired
    private ElasticSearchResponseMapper elasticSearchResponseMapper;

    private static final String TOP_KIND = "search#alphabetical-dissolved";
    private static final int FALLBACK_QUERY_LIMIT = 25;
    private static final String RESULT_FOUND = "A result has been found";
    private static final String SEARCH_HITS = "searchHits";

    public DissolvedSearchResults<DissolvedCompany> getSearchResults(String companyName, String searchBefore, String searchAfter,
            Integer size, String requestId) throws SearchException {
        Map<String, Object> logMap = getLogMap(requestId, companyName);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SEARCH_BEFORE, searchBefore);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SEARCH_AFTER, searchAfter);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SIZE, size);
        LoggingUtils.getLogger().info("getting dissolved search results", logMap);

        String orderedAlphaKey = "";
        List<DissolvedCompany> results = new ArrayList<>();
        DissolvedTopHit topHit = new DissolvedTopHit();
        String etag = GenerateEtagUtil.generateEtag();
        String kind = TOP_KIND;

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(companyName);
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
        }

        try {
            SearchHits hits = getSearchHits(orderedAlphaKey, requestId);

            if (hits.getTotalHits().value == 0) {
                LoggingUtils.getLogger().info("A result was not found, reducing search term to find result", logMap);

                hits = peelbackSearchRequest(hits, orderedAlphaKey, requestId);
            }

            if (hits.getTotalHits().value > 0) {
                LoggingUtils.getLogger().info(RESULT_FOUND, logMap);

                String orderedAlphaKeyWithId;
                SearchHit bestMatch;

                orderedAlphaKeyWithId = SearchRequestUtils.getOrderedAlphaKeyWithId(hits.getHits()[0]);
                bestMatch = hits.getHits()[0];

                DissolvedCompany topHitCompany = elasticSearchResponseMapper.mapDissolvedResponse(bestMatch);

                topHit = elasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany);

                if ((searchBefore == null && searchAfter == null) || (searchBefore != null && searchAfter != null)) {
                    results = populateAboveResults(requestId, topHit.getCompanyName(), orderedAlphaKeyWithId, size);
                    results.add(topHitCompany);
                    results.addAll(
                            populateBelowResults(requestId, topHit.getCompanyName(), orderedAlphaKeyWithId, size));
                } else if (searchAfter != null) {
                    results.addAll(populateBelowResults(requestId, topHit.getCompanyName(), searchAfter, size));
                } else {
                    results.addAll(populateAboveResults(requestId, topHit.getCompanyName(), searchBefore, size));
                }
            }
        } catch (IOException e) {
            LoggingUtils.getLogger().error("failed to map highest map to company object", logMap);
            throw new SearchException("error occurred reading data for highest match from " + SEARCH_HITS, e);
        }

        return new DissolvedSearchResults<DissolvedCompany>(etag, topHit, results, kind);
    }

    public DissolvedSearchResults<DissolvedCompany> getBestMatchSearchResults(String companyName,
                                                            String requestId,
                                                            String searchType,
                                                            Integer startIndex) throws SearchException {
        Map<String, Object> logMap = getLogMap(requestId, companyName);
        LoggingUtils.getLogger().info("getting dissolved " + searchType + " search results", logMap);

        String etag = GenerateEtagUtil.generateEtag();
        DissolvedTopHit topHit = new DissolvedTopHit();
        List<DissolvedCompany> results = new ArrayList<>();
        String kind = "search#dissolved";
        long numberOfHits;

        try {
            SearchHits hits  = dissolvedSearchRequests.getDissolved(companyName, requestId, searchType, startIndex);
            numberOfHits = hits.getTotalHits().value;

            if (hits.getTotalHits().value > 0) {
                LoggingUtils.getLogger().info(RESULT_FOUND, logMap);

                DissolvedCompany topHitCompany = elasticSearchResponseMapper.mapDissolvedResponse(hits.getHits()[0]);

                topHit = elasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany);

                hits.forEach(h -> results.add(elasticSearchResponseMapper.mapDissolvedResponse(h)));
            }
        } catch (IOException e) {
            LoggingUtils.getLogger().error("failed to get best match for dissolved company", logMap);
            throw new SearchException("error occurred reading data for best match from " + SEARCH_HITS, e);
        }

        DissolvedSearchResults<DissolvedCompany> dissolvedSearchResults =
                new DissolvedSearchResults<>(etag, topHit, results, kind);
        dissolvedSearchResults.setHits(numberOfHits);

        return dissolvedSearchResults;
    }

    public DissolvedSearchResults<DissolvedPreviousName> getPreviousNamesResults(String companyName,
                                                                                 String requestId,
                                                                                 String searchType,
                                                                                 Integer startIndex) throws SearchException {
        Map<String, Object> logMap = getLogMap(requestId, companyName);
        LoggingUtils.getLogger().info("getting dissolved " + searchType + " search results", logMap);

        String etag = GenerateEtagUtil.generateEtag();
        PreviousNamesTopHit topHit = new PreviousNamesTopHit();
        List<DissolvedPreviousName> results = new ArrayList<>();
        String kind = "search#previous-name-dissolved";
        long numberOfHits;

        try {
            SearchHits hits  = dissolvedSearchRequests.getDissolved(companyName, requestId, searchType, startIndex);
            numberOfHits = hits.getTotalHits().value;

            if (hits.getTotalHits().value > 0) {
                LoggingUtils.getLogger().info(RESULT_FOUND, logMap);

                results = elasticSearchResponseMapper.mapPreviousNames(hits);
                topHit = elasticSearchResponseMapper.mapPreviousNamesTopHit(results);

            }
        } catch (IOException e) {
            LoggingUtils.getLogger().error("failed to get previous names for dissolved company", logMap);
            throw new SearchException("error occurred reading data for previous names from " + SEARCH_HITS, e);
        }

        DissolvedSearchResults<DissolvedPreviousName> dissolvedSearchResults =
                new DissolvedSearchResults<>(etag, topHit, results, kind);
        dissolvedSearchResults.setHits(numberOfHits);

        return dissolvedSearchResults;
    }

    private SearchHits getSearchHits(String orderedAlphakey, String requestId) throws IOException {
        SearchHits hits = dissolvedSearchRequests.getBestMatchResponse(orderedAlphakey, requestId);

        if (hits.getTotalHits().value == 0) {
            hits = dissolvedSearchRequests.getStartsWithResponse(orderedAlphakey, requestId);
        }

        if (hits.getTotalHits().value == 0) {
            hits = dissolvedSearchRequests.getCorporateNameStartsWithResponse(orderedAlphakey, requestId);
        }
        return hits;
    }

    public SearchHits peelbackSearchRequest(SearchHits hits, String orderedAlphaKey, String requestId)
            throws IOException {
        for (int i = 0; i < orderedAlphaKey.length(); i++) {

            if (hits.getTotalHits().value > 0 || i == FALLBACK_QUERY_LIMIT) {
                return hits;
            }

            if (i != orderedAlphaKey.length() - 1) {
                String resultString = orderedAlphaKey.substring(0, orderedAlphaKey.length() - i);
                hits = getSearchHits(resultString, requestId);
            }
        }
        return hits;
    }

    private List<DissolvedCompany> populateBelowResults(String requestId, String topHitCompanyName,
            String orderedAlphaKeyWithId, Integer size) throws IOException {
        List<DissolvedCompany> results = new ArrayList<>();
        SearchHits hits;
        hits = dissolvedSearchRequests.getDescendingResultsResponse(requestId, orderedAlphaKeyWithId, topHitCompanyName,
                size);
        hits.forEach(h -> results.add(elasticSearchResponseMapper.mapDissolvedResponse(h)));
        return results;
    }

    private List<DissolvedCompany> populateAboveResults(String requestId, String topHitCompanyName,
            String orderedAlphaKeyWithId, Integer size) throws IOException {
        List<DissolvedCompany> results = new ArrayList<>();
        SearchHits hits;
        hits = dissolvedSearchRequests.getAboveResultsResponse(requestId, orderedAlphaKeyWithId, topHitCompanyName,
                size);
        hits.forEach(h -> results.add(elasticSearchResponseMapper.mapDissolvedResponse(h)));

        Collections.reverse(results);
        return results;
    }

    private Map<String, Object> getLogMap(String requestId, String companyName) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, companyName);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);

        return logMap;
    }
}
