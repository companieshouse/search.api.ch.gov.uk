package uk.gov.companieshouse.search.api.elasticsearch;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;

public abstract class AbstractSearchRequest {
    
    abstract String getIndex();
    
    abstract String getResultsSize();
    
    abstract RestClientService getRestClientService();
    
    abstract AbstractSearchQuery getSearchQuery();
    
    @Autowired
    private EnvironmentReader environmentReader;
    
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    

    public SearchHits getBestMatchResponse(String orderedAlphakey, String requestId) throws IOException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.ORDERED_ALPHAKEY, orderedAlphakey);
        LoggingUtils.getLogger().info("Searching for best company match", logMap);
        SearchRequest searchRequestBestMatch = createBaseSearchRequest(requestId);
        searchRequestBestMatch.source(bestMatchSourceBuilder(
                getSearchQuery().createOrderedAlphaKeySearchQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        SearchResponse searchResponse = getRestClientService().search(searchRequestBestMatch);
        return searchResponse.getHits();
    }

    public SearchHits getStartsWithResponse(String orderedAlphakey, String requestId) throws IOException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.ORDERED_ALPHAKEY, orderedAlphakey);
        LoggingUtils.getLogger().info("Searching using alphakey prefix", logMap);
        
        SearchRequest searchRequestStartsWith = createBaseSearchRequest(requestId);

        searchRequestStartsWith.source(bestMatchSourceBuilder(
                getSearchQuery().createOrderedAlphaKeyKeywordQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        SearchResponse searchResponse = getRestClientService().search(searchRequestStartsWith);
        return searchResponse.getHits();
    }

    public SearchHits getCorporateNameStartsWithResponse(String orderedAlphakey,
        String requestId) throws IOException {
        
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.ORDERED_ALPHAKEY, orderedAlphakey);
        LoggingUtils.getLogger().info("Searching using orderedAlphaKey", logMap);

        SearchRequest searchRequestCorporateName = createBaseSearchRequest(requestId);

        // Consider using corporateName instead of orderedAlphakey
        // Currently using same logic as python application
        searchRequestCorporateName.source(bestMatchSourceBuilder(
                getSearchQuery().createStartsWithQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        SearchResponse searchResponse = getRestClientService().search(searchRequestCorporateName);
        return searchResponse.getHits();
    }

    public SearchHits getAboveResultsResponse(String requestId,
        String orderedAlphakeyWithId,
        String topHitCompanyName, Integer size) throws IOException {
        
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.ORDERED_ALPHAKEY_WITH_ID, orderedAlphakeyWithId);
        logMap.put(LoggingUtils.COMPANY_NAME, topHitCompanyName);
        LoggingUtils.getLogger().info("Retrieving the alphabetically descending results", logMap);

        SearchRequest searchAlphabetic = createBaseSearchRequest(requestId);
        searchAlphabetic.source(alphabeticalSourceBuilder(orderedAlphakeyWithId,
                getSearchQuery().createMatchAllQuery(), SortOrder.DESC, size));

        SearchResponse searchResponse = getRestClientService().search(searchAlphabetic);
        return searchResponse.getHits();
    }

    public SearchHits getDescendingResultsResponse(String requestId,
        String orderedAlphakeyWithId,
        String topHitCompanyName, Integer size) throws IOException {
        
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.ORDERED_ALPHAKEY_WITH_ID, orderedAlphakeyWithId);
        logMap.put(LoggingUtils.COMPANY_NAME, topHitCompanyName);
        LoggingUtils.getLogger().info("Retrieving the alphabetically ascending results", logMap);

        SearchRequest searchAlphabetic = createBaseSearchRequest(requestId);
        searchAlphabetic.source(alphabeticalSourceBuilder(orderedAlphakeyWithId,
                getSearchQuery().createMatchAllQuery(), SortOrder.ASC, size));

        SearchResponse searchResponse = getRestClientService().search(searchAlphabetic);
        return searchResponse.getHits();
    }

    private SearchRequest createBaseSearchRequest(String requestId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(getIndex()));
        searchRequest.preference(requestId);

        return searchRequest;
    }

    private SearchSourceBuilder bestMatchSourceBuilder(QueryBuilder queryBuilder, String sortField, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(getResultsSize())));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.sort(sortField, sortOrder);

        return sourceBuilder;
    }

    private SearchSourceBuilder alphabeticalSourceBuilder(String orderedAlphakeyWithId, QueryBuilder queryBuilder, SortOrder sortOrder, Integer size) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if(size != null) {
            sourceBuilder.size(size.intValue());
        } else {
            sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(getResultsSize())));
        }
        sourceBuilder.query(queryBuilder);
        sourceBuilder.searchAfter(new Object[]{orderedAlphakeyWithId});
        sourceBuilder.sort(ORDERED_ALPHA_KEY_WITH_ID, sortOrder);

        return sourceBuilder;
    }

}
