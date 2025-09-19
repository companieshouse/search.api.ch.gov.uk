package uk.gov.companieshouse.search.api.service.search.dissolved;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.search.api.service.rest.impl.DissolvedSearchRestClientService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

 class DissolvedSearchRestClientServiceTest {

    private RestHighLevelClient mockDissolvedRestClient;
    private DissolvedSearchRestClientService service;

    @BeforeEach
    void setUp() {
        mockDissolvedRestClient = mock(RestHighLevelClient.class);
        service = new DissolvedSearchRestClientService(mockDissolvedRestClient);
    }

    @Test
    void searchClient() throws IOException {
        SearchRequest request = mock(SearchRequest.class);
        SearchResponse response = mock(SearchResponse.class);
        when(mockDissolvedRestClient.search(eq(request), any())).thenReturn(response);

        SearchResponse result = service.search(request);

        assertSame(response, result);
        verify(mockDissolvedRestClient).search(eq(request), any());
    }

    @Test
    void upsertClient() throws IOException {
        UpdateRequest request = mock(UpdateRequest.class);
        UpdateResponse response = mock(UpdateResponse.class);
        when(mockDissolvedRestClient.update(eq(request), any())).thenReturn(response);

        UpdateResponse result = service.upsert(request);

        assertSame(response, result);
        verify(mockDissolvedRestClient).update(eq(request), any());
    }
}
