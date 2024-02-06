package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;

import java.io.IOException;
import java.util.Map;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Component
public class AdvancedSearchRequests {

    private final AdvancedSearchRestClientService restClientService;
    private final AdvancedSearchQueries advancedSearchQueries;
    private final ConfiguredIndexNamesProvider indices;

    public AdvancedSearchRequests(AdvancedSearchRestClientService restClientService,
        AdvancedSearchQueries advancedSearchQueries, ConfiguredIndexNamesProvider indices) {
        this.restClientService = restClientService;
        this.advancedSearchQueries = advancedSearchQueries;
        this.indices = indices;
    }

    public SearchHits getCompanies(AdvancedSearchQueryParams queryParams, String requestId) throws IOException {
        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .build().getLogMap();
        LoggingUtils.getLogger().info("Building advanced search request", logMap);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indices.advanced());

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.size(queryParams.getSize());
        sourceBuilder.from(queryParams.getStartIndex());
        sourceBuilder.trackTotalHits(true);

        searchRequest.source(sourceBuilder.query(advancedSearchQueries.buildAdvancedSearchQuery(queryParams)));

        SearchResponse searchResponse = restClientService.search(searchRequest);

        return searchResponse.getHits();
    }
}
