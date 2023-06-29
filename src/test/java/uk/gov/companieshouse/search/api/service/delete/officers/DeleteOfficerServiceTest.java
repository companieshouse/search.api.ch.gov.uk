package uk.gov.companieshouse.search.api.service.delete.officers;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.index.shard.ShardId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteOfficerServiceTest {
    private final String OFFICER_ID = "ABCD1234";
    private final DeleteRequest REQUEST = new DeleteRequest();
    private final String INDEX = "primary_search";

    @Mock
    PrimarySearchRestClientService primarySearchRestClientService;

    @Mock
    OfficerDeleteRequestService officerDeleteRequestService;

    @InjectMocks
    DeleteOfficerService deleteOfficerService;

    @BeforeEach
    void setup() {
        when(officerDeleteRequestService.createDeleteRequest(OFFICER_ID)).thenReturn(REQUEST);
    }
    @Test
    @DisplayName("Test returns DOCUMENTED_DELETED response when officer successfully deleted")
    void returnsDocumentDeleteWhenOfficerIsDeleted() throws Exception {
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, true);
        when(primarySearchRestClientService.delete(REQUEST)).thenReturn(deleteResponse);

        ResponseObject response = deleteOfficerService.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.DOCUMENT_DELETED, response.getStatus());
    }

    @Test
    @DisplayName("Test returns DELETE_NOT_FOUND response when given officer does not exist")
    void returnsDeleteNotFoundWhenOfficerDoesNotExist() throws Exception {
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, false);
        when(primarySearchRestClientService.delete(REQUEST)).thenReturn(deleteResponse);

        ResponseObject response = deleteOfficerService.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.DELETE_NOT_FOUND, response.getStatus());
    }

    @Test
    @DisplayName("Test returns SERVICE_UNAVAILABLE response if IOException is encountered")
    void returnsServiceUnavailableOnIOException() throws Exception {

        when(primarySearchRestClientService.delete(REQUEST)).thenThrow(new IOException());

        ResponseObject response = deleteOfficerService.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.SERVICE_UNAVAILABLE, response.getStatus());
    }

    @Test
    @DisplayName("Test returns DELETE_REQUEST_ERROR response if ElasticsearchException is encountered")
    void returnsDeleteRequestErrorOnElasticSearchException() throws Exception {

        when(primarySearchRestClientService.delete(REQUEST)).thenThrow(new ElasticsearchException(""));

        ResponseObject response = deleteOfficerService.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.DELETE_REQUEST_ERROR, response.getStatus());
    }
}
