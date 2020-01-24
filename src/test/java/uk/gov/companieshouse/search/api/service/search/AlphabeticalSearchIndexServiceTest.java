package uk.gov.companieshouse.search.api.service.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ContextParser;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.NamedXContentRegistry.Entry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlphabeticalSearchIndexServiceTest {

    @InjectMocks
    private SearchIndexService searchIndexService = new AlphabeticalSearchIndexService();

    @Mock
    private RestClientService mockRestClientService;

    @Mock
    private SearchRequestService mockSearchRequestService;

    private static final String TOP_HIT = "AAAA COMMUNICATIONS LIMITED";
    private static final String REQUEST_ID = "requestId";
    private static final String CORPORATE_NAME = "corporateName";

    @Test
    @DisplayName("Test Service Exception thrown no aggregation present")
    public void testServiceExceptionThrownWhenHNoAggregationPresent() throws IOException {

        SearchResponse searchResponse = getSearchResponse("json/searchFailedNoAggregations.json");

        when(mockSearchRequestService.createSearchRequest(CORPORATE_NAME, REQUEST_ID)).thenReturn(new SearchRequest());
        when(mockRestClientService.searchRestClient(any(SearchRequest.class))).thenReturn(searchResponse);

        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test Service Exception thrown aggregation present and highest match not found ")
    public void testServiceExceptionThrownAggregationPresent() throws IOException {

        SearchResponse searchResponse = getSearchResponse("json/searchFailedAggregationNoMatch" +
            ".json");

        when(mockSearchRequestService.createSearchRequest(CORPORATE_NAME, REQUEST_ID)).thenReturn(new SearchRequest());
        when(mockRestClientService.searchRestClient(any(SearchRequest.class))).thenReturn(searchResponse);

        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test error thrown when searchRestClientService fails")
    public void testErrorThrownWhenSearchRestClientFails() throws IOException {
        
        when(mockSearchRequestService.createSearchRequest(CORPORATE_NAME, REQUEST_ID)).thenReturn(new SearchRequest());
        when(mockRestClientService.searchRestClient(any(SearchRequest.class))).thenThrow(new IOException());

        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search not found returned when searchResults empty")
    public void testSearchNotFoundReturned() throws IOException {

        SearchResponse searchResponse = getSearchResponse("json/searchEmptyResults.json");

        when(mockSearchRequestService.createSearchRequest(CORPORATE_NAME, REQUEST_ID)).thenReturn(new SearchRequest());
        when(mockRestClientService.searchRestClient(any(SearchRequest.class))).thenReturn(searchResponse);

        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test successful search found")
    public void testSuccessfulSearchFound() throws IOException {

        SearchResponse searchResponse = getSearchResponse("json/searchSuccessful.json");

        when(mockSearchRequestService.createSearchRequest(CORPORATE_NAME, REQUEST_ID)).thenReturn(new SearchRequest());
        when(mockRestClientService.searchRestClient(any(SearchRequest.class))).thenReturn(searchResponse);

        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, REQUEST_ID);

        SearchResults searchResults = responseObject.getData();

        assertNotNull(responseObject);
        assertNotNull(searchResponse);

        assertEquals(SEARCH_FOUND, responseObject.getStatus());
        assertEquals(TOP_HIT, searchResults.getTopHit());
        assertTrue(searchResults.getResults().size() > 0);
    }

    @Test
    @DisplayName("Test ObjectMapperException thrown")
    public void testObjectMapperExceptionThrown() {
    }

    private SearchResponse getSearchResponse(String jsonFileLocation) throws IOException {

        Resource resource = new ClassPathResource(jsonFileLocation);
        File file = resource.getFile();
        String jsonResponse = new String(Files.readAllBytes(file.toPath()));
        return getSearchResponseFromJson(jsonResponse);
    }

    private SearchResponse getSearchResponseFromJson(String jsonResponse) throws IOException {

        NamedXContentRegistry registry = new NamedXContentRegistry(getDefaultNamedXContents());
        XContentParser parser =
            JsonXContent.jsonXContent.createParser(registry,
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION, jsonResponse);

        SearchResponse searchResponse = SearchResponse.fromXContent(parser);


        return searchResponse;
    }

    private List<Entry> getDefaultNamedXContents() {
        Map<String, ContextParser<Object, ? extends Aggregation>> map = new HashMap<>();
        map.put(TopHitsAggregationBuilder.NAME, (p, c) -> ParsedTopHits.fromXContent(p, (String) c));
        map.put(StringTerms.NAME, (p, c) -> ParsedStringTerms.fromXContent(p, (String) c));
        List<NamedXContentRegistry.Entry> entries = map.entrySet().stream()
            .map(entry -> new NamedXContentRegistry.Entry(Aggregation.class,
                new ParseField(entry.getKey()), entry.getValue()))
            .collect(Collectors.toList());

        return entries;
    }
}
