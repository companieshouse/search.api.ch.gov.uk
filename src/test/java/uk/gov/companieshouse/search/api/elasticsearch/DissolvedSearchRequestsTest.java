package uk.gov.companieshouse.search.api.elasticsearch;


import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchResponseSections;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.DissolvedSearchRestClientService;

import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DissolvedSearchRequestsTest {

    @InjectMocks
    DissolvedSearchRequests dissolvedSearchRequests;

    @Mock
    private DissolvedSearchRestClientService mockSearchRestClient;

    @Mock
    private DissolvedSearchQueries mockDissolvedSearchQueries;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    private static final String ENV_READER_RESULT = "1";

    @Test
    @DisplayName("Get best match response")
    void getBestMatchResponse() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT);
        when(mockSearchRestClient.search(any(SearchRequest.class))).thenReturn(createSearchResponse());

        SearchHits searchHits = dissolvedSearchRequests
                .getBestMatchResponse("orderedAlpha", "requestId");

        assertNotNull(searchHits);
        assertEquals(1, searchHits.getTotalHits().value);
    }

    @Test
    @DisplayName("Get starts with response")
    void getStartsWithResponse() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT);
        when(mockSearchRestClient.search(any(SearchRequest.class))).thenReturn(createSearchResponse());

        SearchHits searchHits = dissolvedSearchRequests
                .getStartsWithResponse("orderedAlpha", "requestId");

        assertNotNull(searchHits);
        assertEquals(1, searchHits.getTotalHits().value);
    }

    @Test
    @DisplayName("Get company name starts with response")
    void getCompanyNameStartsWithResponse() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT);
        when(mockSearchRestClient.search(any(SearchRequest.class))).thenReturn(createSearchResponse());

        SearchHits searchHits = dissolvedSearchRequests
                .getCorporateNameStartsWithResponse("orderedAlpha", "requestId");

        assertNotNull(searchHits);
        assertEquals(1, searchHits.getTotalHits().value);
    }

    @Test
    @DisplayName("Get above results response")
    void getAboveResultsResponse() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT);
        when(mockSearchRestClient.search(any(SearchRequest.class))).thenReturn(createSearchResponse());

        SearchHits searchHits = dissolvedSearchRequests
                .getAboveResultsResponse("requestId",
                        "orderedAlpha", "topHit");

        assertNotNull(searchHits);
        assertEquals(1, searchHits.getTotalHits().value);
    }

    @Test
    @DisplayName("Get descending results response")
    void getDescendingResultsResponse() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT);
        when(mockSearchRestClient.search(any(SearchRequest.class))).thenReturn(createSearchResponse());

        SearchHits searchHits = dissolvedSearchRequests
                .getDescendingResultsResponse("requestId",
                        "orderedAlpha", "topHit");

        assertNotNull(searchHits);
        assertEquals(1, searchHits.getTotalHits().value);
    }

    @Test
    @DisplayName("Get no results found fallback response")
    void noResultsFallbackQueryResponse() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT);
        when(mockSearchRestClient.search(any(SearchRequest.class))).thenReturn(createSearchResponse());

        SearchHits searchHits = dissolvedSearchRequests
                .noResultsFallbackQuery("orderedAlpha", "requestId");

        assertNotNull(searchHits);
        assertEquals(1, searchHits.getTotalHits().value);
    }

    private SearchResponse createSearchResponse() {
        BytesReference source = new BytesArray(
                "{test}" );
        SearchHit hit = new SearchHit( 1 );
        hit.sourceRef( source );
        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        SearchHits hits = new SearchHits( new SearchHit[] { hit }, totalHits, 10 );
        SearchResponseSections searchResponseSections = new SearchResponseSections( hits, null, null, false, null, null, 5 );
        SearchResponse.Clusters clusters = new SearchResponse.Clusters(1, 1, 0);
        SearchResponse searchResponse = new SearchResponse( searchResponseSections, "", 1, 1, 0, 8, new ShardSearchFailure[] {}, clusters );
        return searchResponse;
    }
}
