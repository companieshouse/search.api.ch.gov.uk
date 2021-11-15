package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;

import java.io.IOException;
import java.util.Map;

@Component
public class AdvancedSearchRequests {

    @Autowired
    private AdvancedSearchRestClientService restClientService;

    @Autowired
    private EnvironmentReader environmentReader;

    @Autowired
    private AdvancedSearchQueries advancedSearchQueries;

    private static final String INDEX = "ADVANCED_SEARCH_INDEX";

    public SearchHits getCompanies(AdvancedSearchQueryParams queryParams, String requestId) throws IOException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        LoggingUtils.getLogger().info("Building advanced search request", logMap);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(INDEX));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.size(20);
        sourceBuilder.from(queryParams.getStartIndex());

        searchRequest.source(sourceBuilder.query(advancedSearchQueries.buildAdvancedSearchQuery(queryParams)));

        SearchResponse searchResponse = restClientService.search(searchRequest);

        return searchResponse.getHits();
    }
}
