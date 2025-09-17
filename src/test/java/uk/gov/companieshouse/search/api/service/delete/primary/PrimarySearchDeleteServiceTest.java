package uk.gov.companieshouse.search.api.service.delete.primary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.index.shard.ShardId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.SearchType;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;

import java.io.IOException;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class PrimarySearchDeleteServiceTest {
    private final SearchType searchType = new SearchType("officerId", "disqualified-officer");
    private final String companyNumber = "123456789";
    private static final DeleteRequest REQUEST = new DeleteRequest();
    private static final String INDEX = "primary_search";
    @Mock
    PrimarySearchRestClientService primarySearchRestClientService;
    @Mock
    PrimarySearchDeleteRequestService primarySearchDeleteRequestService;
    @Mock
    private ConfiguredIndexNamesProvider indices;

    @InjectMocks
    PrimarySearchDeleteService service;

    @Test
    void deletesOfficer() throws Exception {
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, true);
        when(primarySearchDeleteRequestService.createDeleteRequest(searchType)).thenReturn(REQUEST);
        when(primarySearchRestClientService.delete(REQUEST)).thenReturn(deleteResponse);

        ResponseObject response = service.deleteOfficer(searchType);

        assertEquals(ResponseStatus.DOCUMENT_DELETED, response.getStatus());
    }



    @Test
    void returnsDeleteNotFoundWhenOfficerDoesNotExist() throws Exception {
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, false);
        when(primarySearchDeleteRequestService.createDeleteRequest(searchType)).thenReturn(REQUEST);
        when(primarySearchRestClientService.delete(REQUEST)).thenReturn(deleteResponse);

        ResponseObject response = service.deleteOfficer(searchType);

        assertEquals(ResponseStatus.DELETE_NOT_FOUND, response.getStatus());
    }

    @Test
    void returnsServiceUnavailableOnIOException() throws Exception {

        when(primarySearchDeleteRequestService.createDeleteRequest(searchType)).thenReturn(REQUEST);
        when(primarySearchRestClientService.delete(REQUEST)).thenThrow(new IOException());

        ResponseObject response = service.deleteOfficer(searchType);

        assertEquals(ResponseStatus.SERVICE_UNAVAILABLE, response.getStatus());
    }

    @Test
    void returnsUpdateErrorOnElasticSearchException() throws Exception {

        when(primarySearchDeleteRequestService.createDeleteRequest(searchType)).thenReturn(REQUEST);
        when(primarySearchRestClientService.delete(REQUEST)).thenThrow(new ElasticsearchException(""));

        ResponseObject response = service.deleteOfficer(searchType);

        assertEquals(ResponseStatus.DELETE_REQUEST_ERROR, response.getStatus());
    }

    @Test
    void testDeleteCompanyByNumber_Success() throws IOException {
        when(indices.primary()).thenReturn("primary_search");

        // Stubbing the delete method to return a mock DeleteResponse
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, true);

        when(primarySearchRestClientService.delete(any(DeleteRequest.class))).thenReturn(deleteResponse);

        ResponseObject response = service.deleteCompanyByNumber(companyNumber);

        assertEquals(ResponseStatus.DOCUMENT_DELETED, response.getStatus());
    }
    @Test
    void testDeleteCompanyByNumber_IOException() throws IOException {
        when(primarySearchRestClientService.delete(any(DeleteRequest.class))).thenThrow(IOException.class);

        ResponseObject response = service.deleteCompanyByNumber(companyNumber);

        assertEquals(ResponseStatus.SERVICE_UNAVAILABLE, response.getStatus());
    }

    @Test
    void testDeleteCompanyByNumber_ElasticsearchException() throws IOException {
        when(primarySearchRestClientService.delete(any(DeleteRequest.class))).thenThrow(ElasticsearchException.class);

        ResponseObject response = service.deleteCompanyByNumber(companyNumber);

        assertEquals(ResponseStatus.DELETE_REQUEST_ERROR, response.getStatus());
    }

    @Test
    void testDeleteCompanyByNumber_NotFound() throws IOException {

        // Stubbing the delete method to return a mock DeleteResponse
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, false);
        when(primarySearchRestClientService.delete(any(DeleteRequest.class))).thenReturn(deleteResponse);

        ResponseObject response = service.deleteCompanyByNumber(companyNumber);

        assertEquals(ResponseStatus.DELETE_NOT_FOUND, response.getStatus());
    }
}
