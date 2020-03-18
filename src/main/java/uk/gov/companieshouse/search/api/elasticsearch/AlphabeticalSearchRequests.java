package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;

import java.io.IOException;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Component
public class AlphabeticalSearchRequests {

    @Autowired
    private RestClientService searchRestClient;

    @Autowired
    private EnvironmentReader environmentReader;

    @Autowired
    private AlphabeticalSearchQueries alphabeticalSearchQueries;

    private static final String INDEX = "ALPHABETICAL_SEARCH_INDEX";
    private static final String RESULTS_SIZE = "ALPHABETICAL_SEARCH_RESULT_MAX";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    public SearchHits getBestMatchResponse(String orderedAlphakey, String requestId) throws IOException {
        SearchRequest searchRequestBestMatch = createBaseSearchRequest(requestId);
        searchRequestBestMatch.source(bestMatchSourceBuilder(
            alphabeticalSearchQueries.createOrderedAlphaKeySearchQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        SearchResponse searchResponse = searchRestClient.searchRestClient(searchRequestBestMatch);
        return searchResponse.getHits();
    }

    public SearchHits getStartsWithResponse(String orderedAlphakey, String requestId) throws IOException {
        LOG.info("A hit was not found for: " + orderedAlphakey + ", falling back to prefix on alphakey");
        SearchRequest searchRequestStartsWith = createBaseSearchRequest(requestId);

        searchRequestStartsWith.source(bestMatchSourceBuilder(
            alphabeticalSearchQueries.createOrderedAlphaKeyKeywordQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        SearchResponse searchResponse = searchRestClient.searchRestClient(searchRequestStartsWith);
        return searchResponse.getHits();
    }

    public SearchHits getCorporateNameStartsWithResponse(String orderedAlphakey,
        String requestId) throws IOException {

        LOG.info("A hit was not found for: " + orderedAlphakey + ", falling back to corporate name");
        SearchRequest searchRequestCorporateName = createBaseSearchRequest(requestId);

        // Consider using corporateName instead of orderedAlphakey
        // Currently using same logic as python application
        searchRequestCorporateName.source(bestMatchSourceBuilder(
            alphabeticalSearchQueries.createStartsWithQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        SearchResponse searchResponse = searchRestClient.searchRestClient(searchRequestCorporateName);
        return searchResponse.getHits();
    }

    public SearchHits getAboveResultsResponse(String requestId,
        String orderedAlphakeyWithId,
        String topHitCompanyName) throws IOException {
        LOG.info("Retrieving the alphabetically descending results for search elasticsearch: " + topHitCompanyName);

        SearchRequest searchAlphabetic = createBaseSearchRequest(requestId);
        searchAlphabetic.source(alphabeticalSourceBuilder(orderedAlphakeyWithId,
            alphabeticalSearchQueries.createAlphabeticalQuery(), SortOrder.DESC));

        SearchResponse searchResponse = searchRestClient.searchRestClient(searchAlphabetic);
        return searchResponse.getHits();
    }

    public SearchHits getDescendingResultsResponse(String requestId,
        String orderedAlphakeyWithId,
        String topHitCompanyName) throws IOException {

        LOG.info("Retrieving the alphabetically ascending results from: " + topHitCompanyName);
        SearchRequest searchAlphabetic = createBaseSearchRequest(requestId);
        searchAlphabetic.source(alphabeticalSourceBuilder(orderedAlphakeyWithId,
            alphabeticalSearchQueries.createAlphabeticalQuery(), SortOrder.ASC));

        SearchResponse searchResponse = searchRestClient.searchRestClient(searchAlphabetic);
        return searchResponse.getHits();
    }

    private SearchRequest createBaseSearchRequest(String requestId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(INDEX));
        searchRequest.preference(requestId);

        return searchRequest;
    }

    private SearchSourceBuilder bestMatchSourceBuilder(QueryBuilder queryBuilder, String sortField, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.sort(sortField, sortOrder);

        return sourceBuilder;
    }

    private SearchSourceBuilder alphabeticalSourceBuilder(String orderedAlphakeyWithId, QueryBuilder queryBuilder, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.searchAfter(new Object[]{orderedAlphakeyWithId});
        sourceBuilder.sort(ORDERED_ALPHA_KEY_WITH_ID, sortOrder);

        return sourceBuilder;
    }
}
