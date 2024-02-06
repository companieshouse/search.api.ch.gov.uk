package uk.gov.companieshouse.search.api.service.upsert.officers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class UpsertOfficersServiceTest {

    private static final String OFFICER_ID = "officerId";

    @Mock
    private PrimarySearchRestClientService primarySearchRestClientService;
    @Mock
    private OfficersUpsertRequestService officersUpsertRequestService;
    @InjectMocks
    private UpsertOfficersService service;
    @Mock
    private AppointmentList appointmentList;
    @Mock
    private UpdateRequest request;
    @Mock
    private ConfiguredIndexNamesProvider indices;

    @Test
    void officerIsUpsertedCorrectly() throws Exception {
        when(officersUpsertRequestService.createUpdateRequest(any(), anyString())).thenReturn(request);

        ResponseObject response = service.upsertOfficers(appointmentList, OFFICER_ID);

        assertEquals(ResponseStatus.DOCUMENT_UPSERTED, response.getStatus());
        verify(primarySearchRestClientService).upsert(request);
    }

    @Test
    void officerReturnsUpsertErrorIfUpsertException() throws Exception {
        when(officersUpsertRequestService.createUpdateRequest(appointmentList, OFFICER_ID)).thenThrow(new UpsertException(""));

        ResponseObject response = service.upsertOfficers(appointmentList, OFFICER_ID);

        assertEquals(ResponseStatus.UPSERT_ERROR, response.getStatus());
    }

    @Test
    void officerReturnsServiceUnavailableIfIOException() throws Exception {
        when(officersUpsertRequestService.createUpdateRequest(appointmentList, OFFICER_ID)).thenReturn(request);
        when(primarySearchRestClientService.upsert(request)).thenThrow(new IOException(""));

        ResponseObject response = service.upsertOfficers(appointmentList, OFFICER_ID);

        assertEquals(ResponseStatus.SERVICE_UNAVAILABLE, response.getStatus());
    }

    @Test
    void officerReturnsUpdateErrorIfBadRequest() throws Exception {
        when(officersUpsertRequestService.createUpdateRequest(appointmentList, OFFICER_ID)).thenReturn(request);
        when(primarySearchRestClientService.upsert(request)).thenThrow(new ElasticsearchException(""));

        ResponseObject response = service.upsertOfficers(appointmentList, OFFICER_ID);

        assertEquals(ResponseStatus.UPDATE_REQUEST_ERROR, response.getStatus());
    }
}
