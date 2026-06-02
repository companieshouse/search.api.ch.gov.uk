package uk.gov.companieshouse.search.api.opensearch;

import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch._types.SortOrder;


import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.FieldValue;

import org.opensearch.client.opensearch.core.search.HitsMetadata;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.service.rest.OpenSearchRestClientService;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractOpenSearchRequest {
    abstract String getIndex();

    abstract String getResultsSize();

    abstract OpenSearchRestClientService getRestClientService();

    abstract AbstractOpenSearchQuery getSearchQuery();

    protected final EnvironmentReader environmentReader;

    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";

    protected AbstractOpenSearchRequest(EnvironmentReader environmentReader) {
        this.environmentReader = environmentReader;
    }

    public HitsMetadata<Object> getBestMatchResponse(String orderedAlphakey, String requestId) throws IOException {
        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .orderedAlphakey(orderedAlphakey)
                .build().getLogMap();
        LoggingUtils.getLogger().info("Searching OpenSearch cluster for best company match", logMap);

        SearchRequest searchRequest = bestMatchSourceBuilder(
                getSearchQuery().createOrderedAlphaKeySearchQuery(orderedAlphakey),
                ORDERED_ALPHA_KEY_WITH_ID, SortOrder.Asc);

        SearchRequest searchRequestBestMatch = createBaseOpenSearchRequest(searchRequest, requestId);

        SearchResponse<Object> searchResponse = getRestClientService().search(searchRequestBestMatch);
        return searchResponse.hits();
    }

    public HitsMetadata<Object> getStartsWithResponse(String orderedAlphakey, String requestId) throws IOException {
        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .orderedAlphakey(orderedAlphakey)
                .build().getLogMap();
        LoggingUtils.getLogger().info("Searching OpenSearch cluster using alphakey prefix", logMap);

        SearchRequest searchRequest = bestMatchSourceBuilder(
                getSearchQuery().createOrderedAlphaKeyKeywordQuery(orderedAlphakey),
                ORDERED_ALPHA_KEY_WITH_ID, SortOrder.Asc);

        SearchRequest searchRequestStartsWith = createBaseOpenSearchRequest(searchRequest, requestId);

        SearchResponse<Object> searchResponse = getRestClientService().search(searchRequestStartsWith);
        return searchResponse.hits();
    }

    public HitsMetadata<Object> getCorporateNameStartsWithResponse(String orderedAlphakey,
        String requestId) throws IOException {

        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .orderedAlphakey(orderedAlphakey)
                .build().getLogMap();
        LoggingUtils.getLogger().info("Searching OpenSearch cluster using orderedAlphaKey", logMap);


        // Consider using corporateName instead of orderedAlphakey
        // Currently using same logic as python application
        SearchRequest searchRequest = bestMatchSourceBuilder(
                getSearchQuery().createStartsWithQuery(orderedAlphakey),
                ORDERED_ALPHA_KEY_WITH_ID, SortOrder.Asc);

        SearchRequest searchRequestCorporateName = createBaseOpenSearchRequest(searchRequest, requestId);


        SearchResponse<Object> searchResponse = getRestClientService().search(searchRequestCorporateName);
        return searchResponse.hits();
    }

    public HitsMetadata<Object> getAboveResultsResponse(String requestId,
                                                     String orderedAlphakeyWithId,
                                                     String topHitCompanyName, Integer size) throws IOException {

        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .orderedAlphakeyWithId(orderedAlphakeyWithId)
                .companyName(topHitCompanyName)
                .size(String.valueOf(size))
                .build().getLogMap();
        LoggingUtils.getLogger().info("Retrieving the alphabetically descending results from OpenSearch cluster", logMap);


        SearchRequest searchRequest = alphabeticalSearchRequest(orderedAlphakeyWithId,
                getSearchQuery().createMatchAllQuery(), SortOrder.Desc, size);

        SearchRequest searchAlphabetic = createBaseOpenSearchRequest(searchRequest, requestId);

        SearchResponse<Object> searchResponse = getRestClientService().search(searchAlphabetic);
        return searchResponse.hits();
    }

    public HitsMetadata<Object> getDescendingResultsResponse(String requestId,
        String orderedAlphakeyWithId,
        String topHitCompanyName, Integer size) throws IOException {

        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .orderedAlphakeyWithId(orderedAlphakeyWithId)
                .companyName(topHitCompanyName)
                .size(String.valueOf(size))
                .build().getLogMap();
        LoggingUtils.getLogger().info("Retrieving the alphabetically ascending results from OpenSearch cluster", logMap);

        SearchRequest searchRequest = alphabeticalSearchRequest(orderedAlphakeyWithId,
                getSearchQuery().createMatchAllQuery(), SortOrder.Asc, size);

        SearchRequest searchAlphabetic = createBaseOpenSearchRequest(searchRequest, requestId);

        SearchResponse<Object>searchResponse = getRestClientService().search(searchAlphabetic);
        return searchResponse.hits();
    }


    private SearchRequest createBaseOpenSearchRequest(SearchRequest searchRequest, String requestId) {
        return searchRequest.toBuilder()
                .index(environmentReader.getMandatoryString(getIndex()))
                .preference(requestId)
                .build();
    }


    private SearchRequest bestMatchSourceBuilder(Query query, String sortField, SortOrder sortOrder) {

        int size = Integer.parseInt(environmentReader.getMandatoryString(getResultsSize()));

        return SearchRequest.of(s -> s
                .size(size)
                .query(query)
                .sort(so -> so
                        .field(f -> f
                                .field(sortField)
                                .order(sortOrder)
                        )
                )
        );
    }

    public SearchRequest alphabeticalSearchRequest(
            String orderedAlphakeyWithId,
            Query query,
            SortOrder sortOrder,
            Integer size) {

        int finalSize = (size != null)
                ? size
                : Integer.parseInt(environmentReader.getMandatoryString(getResultsSize()));

        return new SearchRequest.Builder()
                .size(finalSize)
                .query(query)
                .searchAfter(FieldValue.of(orderedAlphakeyWithId))
                .sort(s -> s
                        .field(f -> f
                                .field(ORDERED_ALPHA_KEY_WITH_ID)
                                .order(sortOrder)
                        )
                )
                .build();
    }

}
