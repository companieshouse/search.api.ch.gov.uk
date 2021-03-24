package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.DissolvedSearchRestClientService;

import java.io.IOException;
import java.util.Map;

@Component
public class DissolvedSearchRequests extends AbstractSearchRequest {

    @Autowired
    private DissolvedSearchRestClientService searchRestClient;

    @Autowired
    private DissolvedSearchQueries searchQueries;

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String INDEX = "DISSOLVED_SEARCH_INDEX";
    private static final String RESULTS_SIZE = "DISSOLVED_SEARCH_RESULT_MAX";

    @Override
    String getIndex() {
        return INDEX;
    }

    @Override
    String getResultsSize() {
        return RESULTS_SIZE;
    }

    @Override
    RestClientService getRestClientService() {
        return searchRestClient;
    }

    @Override
    AbstractSearchQuery getSearchQuery() {
        return searchQueries;
    }

    public SearchHits getDissolved(String companyName, String requestId) throws IOException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, companyName);
        LoggingUtils.getLogger().info("Searching for best dissolved company name match", logMap);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(getIndex()));
        searchRequest.preference(requestId);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(getResultsSize())));
        sourceBuilder.query(searchQueries.createBestMatchQuery(companyName));
        sourceBuilder.sort("ordered_alpha_key_with_id", SortOrder.ASC);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = searchRestClient.search(searchRequest);
        return searchResponse.getHits();
    }
}
