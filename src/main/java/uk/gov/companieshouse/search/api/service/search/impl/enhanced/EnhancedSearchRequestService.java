package uk.gov.companieshouse.search.api.service.search.impl.enhanced;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.ENHANCED_SEARCH_INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.search.api.elasticsearch.EnhancedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;

@Service
public class EnhancedSearchRequestService {

    @Autowired
    private EnhancedSearchRequests enhancedSearchRequests;

    @Autowired
    private ElasticSearchResponseMapper elasticSearchResponseMapper;


    private static final String RESULT_FOUND = "A result has been found";

    public SearchResults<Company> getSearchResults(EnhancedSearchQueryParams queryParams, String requestId) throws SearchException {

        Map<String, Object> logMap = getLogMap(requestId);
        getLogger().info("Getting enhanced search results", logMap);
        logMap.remove(MESSAGE);

        String etag = GenerateEtagUtil.generateEtag();
        TopHit topHit = new TopHit();
        List<Company> results = new ArrayList<>();
        String kind = "search#enhanced-search";

        try {
            SearchHits hits = enhancedSearchRequests.getCompanies(queryParams, requestId);

            if (hits.getTotalHits().value > 0) {
                getLogger().info(RESULT_FOUND, logMap);

                Company topHitCompany = elasticSearchResponseMapper
                        .mapEnhancedSearchResponse(hits.getHits()[0]);

                topHit = elasticSearchResponseMapper.mapEnhancedTopHit(topHitCompany);

                hits.forEach(h -> results.add(elasticSearchResponseMapper.mapEnhancedSearchResponse(h)));
            }
        } catch (IOException e) {
            getLogger().error("failed to return a company using enhanced search", logMap);
            throw new SearchException("error occurred reading data from the search hits", e);
        }

        return new SearchResults<>(etag, topHit, results, kind);
    }

    private Map<String, Object> getLogMap(String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(INDEX, ENHANCED_SEARCH_INDEX);

        return logMap;
    }
}