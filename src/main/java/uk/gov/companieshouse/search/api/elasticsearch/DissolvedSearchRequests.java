package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
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
    private static final String BEST_MATCH_SEARCH_TYPE = "best-match";

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

    public SearchHits getDissolved(String companyName,
                                   String requestId,
                                   String searchType,
                                   Integer startIndex) throws IOException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, companyName);
        LoggingUtils.getLogger().info("Searching for best dissolved company name " + searchType + " match", logMap);

        SearchRequest searchRequest = getBaseSearchRequest(requestId);

        SearchSourceBuilder sourceBuilder = getBaseSourceBuilder(startIndex);
        if (searchType.equals(BEST_MATCH_SEARCH_TYPE)){
            sourceBuilder.query(searchQueries.createBestMatchQuery(companyName));
        }
        else {
            String[] includes = {"company_name", "company_number", "date_of_creation", "date_of_cessation", "registered_office_address.post_code"};

            sourceBuilder.fetchSource(new FetchSourceContext(true, includes, null));
            sourceBuilder.query(searchQueries.createPreviousNamesBestMatchQuery(companyName));
        }

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = searchRestClient.search(searchRequest);
        return searchResponse.getHits();
    }

    private SearchRequest getBaseSearchRequest(String requestId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(getIndex()));
        searchRequest.preference(requestId);

        return searchRequest;
    }

    private SearchSourceBuilder getBaseSourceBuilder(Integer startIndex) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(getResultsSize())));
        sourceBuilder.from(startIndex);

        return sourceBuilder;
    }
}
