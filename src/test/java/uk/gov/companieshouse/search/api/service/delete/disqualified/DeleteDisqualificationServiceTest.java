package uk.gov.companieshouse.search.api.service.delete.disqualified;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
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
    void deletesOfficer() {

        ResponseObject response = service.deleteOfficer(OFFICER_ID);

        assertEquals(ResponseStatus.DOCUMENT_DELETED, response.getStatus());
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
