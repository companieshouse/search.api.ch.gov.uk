package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;
import uk.gov.companieshouse.search.api.service.rest.impl.EnhancedSearchRestClientService;

import java.io.IOException;
import java.util.Map;

@Component
public class EnhancedSearchRequests {

    @Autowired
    private EnhancedSearchRestClientService restClientService;

    @Autowired
    private EnvironmentReader environmentReader;

    @Autowired
    private EnhancedSearchQueries enhancedSearchQueries;

    private static final String INDEX = "ENHANCED_SEARCH_INDEX";

    public SearchHits getCompanies(EnhancedSearchQueryParams queryParams, String requestId) throws IOException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        LoggingUtils.getLogger().info("Building enhanced search request", logMap);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(INDEX));
        searchRequest.preference(requestId);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        searchRequest.source(sourceBuilder.query(enhancedSearchQueries.buildEnhancedSearchQuery(queryParams)));

        SearchResponse searchResponse = restClientService.search(searchRequest);

        return searchResponse.getHits();
    }
}
