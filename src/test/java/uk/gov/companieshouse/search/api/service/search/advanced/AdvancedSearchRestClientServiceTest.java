package uk.gov.companieshouse.search.api.service.search.advanced;


import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AdvancedSearchRestClientServiceTest {

    private RestHighLevelClient mockAdvancedRestClient;
    private AdvancedSearchRestClientService service;

    @BeforeEach
    void setUp() {
        mockAdvancedRestClient = mock(RestHighLevelClient.class);
        service = new AdvancedSearchRestClientService(mockAdvancedRestClient);
    }

    @Test
    void searchClient() throws IOException {
        SearchRequest request = mock(SearchRequest.class);
        SearchResponse response = mock(SearchResponse.class);
        when(mockAdvancedRestClient.search(eq(request), any())).thenReturn(response);

        SearchResponse result = service.search(request);

        assertSame(response, result);
        verify(mockAdvancedRestClient).search(eq(request), any());
    }

    @Test
    void upsertClient() throws IOException {
        UpdateRequest request = mock(UpdateRequest.class);
        UpdateResponse response = mock(UpdateResponse.class);
        when(mockAdvancedRestClient.update(eq(request), any())).thenReturn(response);

        UpdateResponse result = service.upsert(request);

        assertSame(response, result);
        verify(mockAdvancedRestClient).update(eq(request), any());
    }

    @Test
    void deleteClient() throws IOException {
        DeleteRequest request = mock(DeleteRequest.class);
        DeleteResponse response = mock(DeleteResponse.class);
        when(mockAdvancedRestClient.delete(eq(request), any())).thenReturn(response);

        DeleteResponse result = service.delete(request);

        assertSame(response, result);
        verify(mockAdvancedRestClient).delete(eq(request), any());
    }
}
