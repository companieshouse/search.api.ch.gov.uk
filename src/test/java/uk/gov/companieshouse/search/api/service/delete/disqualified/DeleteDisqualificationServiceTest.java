package uk.gov.companieshouse.search.api.service.delete.disqualified;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.index.shard.ShardId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.DisqualifiedSearchRestClientService;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class DeleteDisqualificationServiceTest {

    private static final String OFFICER_ID = "officerId";
    private static final DeleteRequest REQUEST = new DeleteRequest();
    private static final String INDEX = "primary_search";

    @Mock
    DisqualifiedSearchRestClientService disqualifiedSearchRestClientService;

    @Mock
    DisqualifiedDeleteRequestService disqualifiedDeleteRequestService;

    @InjectMocks
    DeleteDisqualificationService service;

    @BeforeEach
    void setup() {
        when(disqualifiedDeleteRequestService.createDeleteRequest(OFFICER_ID)).thenReturn(REQUEST);
    }

    @Test
    void deletesOfficer() throws Exception {
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, true);
        when(disqualifiedSearchRestClientService.delete(REQUEST)).thenReturn(deleteResponse);

        ResponseObject response = service.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.DOCUMENT_DELETED, response.getStatus());
    }

    @Test
    void returnsDeleteNotFoundWhenOfficerDoesNotExist() throws Exception {
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, false);
        when(disqualifiedSearchRestClientService.delete(REQUEST)).thenReturn(deleteResponse);

        ResponseObject response = service.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.DELETE_NOT_FOUND, response.getStatus());
    }

    @Test
    void returnsServiceUnavailableOnIOException() throws Exception {

        when(disqualifiedSearchRestClientService.delete(REQUEST)).thenThrow(new IOException());

        ResponseObject response = service.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.SERVICE_UNAVAILABLE, response.getStatus());
    }

    @Test
    void returnsUpdateErrorOnElasticSearchException() throws Exception {

        when(disqualifiedSearchRestClientService.delete(REQUEST)).thenThrow(new ElasticsearchException(""));

        ResponseObject response = service.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.DELETE_REQUEST_ERROR, response.getStatus());
    }
}
