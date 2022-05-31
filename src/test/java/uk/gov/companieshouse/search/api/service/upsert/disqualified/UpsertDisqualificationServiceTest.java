package uk.gov.companieshouse.search.api.service.upsert.disqualified;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.DisqualifiedSearchRestClientService;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class UpsertDisqualificationServiceTest {

    private static final String OFFICER_ID = "officerId";
    private static final UpdateRequest request = new UpdateRequest();

    @Mock
    private DisqualifiedSearchRestClientService disqualifiedSearchRestClientService;
    @Mock
    private DisqualifiedUpsertRequestService disqualifiedUpsertRequestService;
    @InjectMocks
    private UpsertDisqualificationService service;

    @Test
    public void disqualificationIsUpsertedCorrectly() throws Exception {
        OfficerDisqualification officer = createOfficer();
        when(disqualifiedUpsertRequestService.createUpdateRequest(officer, OFFICER_ID)).thenReturn(request);

        ResponseObject response = service.upsertDisqualified(officer, OFFICER_ID);

        assertEquals(ResponseStatus.DOCUMENT_UPSERTED, response.getStatus());
        verify(disqualifiedSearchRestClientService).upsert(request);
    }

    @Test
    public void disqualificationReturnsUpsertErrorIfUpsertException() throws Exception {
        OfficerDisqualification officer = createOfficer();
        when(disqualifiedUpsertRequestService.createUpdateRequest(officer, OFFICER_ID)).thenThrow(new UpsertException(""));

        ResponseObject response = service.upsertDisqualified(officer, OFFICER_ID);

        assertEquals(ResponseStatus.UPSERT_ERROR, response.getStatus());
    }

    @Test
    public void disqualificationReturnsUpdateErrorIfIOException() throws Exception {
        OfficerDisqualification officer = createOfficer();
        when(disqualifiedUpsertRequestService.createUpdateRequest(officer, OFFICER_ID)).thenReturn(request);
        when(disqualifiedSearchRestClientService.upsert(request)).thenThrow(new IOException(""));

        ResponseObject response = service.upsertDisqualified(officer, OFFICER_ID);

        assertEquals(ResponseStatus.SERVICE_UNAVAILABLE, response.getStatus());
    }

    private OfficerDisqualification createOfficer() {
        OfficerDisqualification officer = new OfficerDisqualification();
        Item item = new Item();
        item.setForename("Forename");
        item.setSurname("Surname");
        officer.addItemsItem(item);
        return officer;
    }
}
