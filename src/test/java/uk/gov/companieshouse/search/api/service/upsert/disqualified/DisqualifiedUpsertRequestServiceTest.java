package uk.gov.companieshouse.search.api.service.upsert.disqualified;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.elasticsearch.DisqualifiedSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class DisqualifiedUpsertRequestServiceTest {

    private static final String UPDATE_JSON = "{\"example\":\"test\"}";
    private static final String OFFICER_ID = "testid";

    @Mock
    private DisqualifiedSearchUpsertRequest disqualifiedSearchUpsertRequest;
    @InjectMocks
    private DisqualifiedUpsertRequestService service;

    @Test
    public void serviceCreatesUpdateRequest() throws Exception {
        OfficerDisqualification officer = createOfficer();
        when(disqualifiedSearchUpsertRequest.buildRequest(officer)).thenReturn(UPDATE_JSON);

        UpdateRequest request = service.createUpdateRequest(officer, OFFICER_ID);

        assertEquals(OFFICER_ID, request.id());
        String expected = "update {[primary_search][primary_search][" + OFFICER_ID +
                "], doc_as_upsert[true], doc[index {[null][_doc][null], source[" + UPDATE_JSON +
                "]}], scripted_upsert[false], detect_noop[true]}";
        assertEquals(expected, request.toString());
    }

    @Test
    public void serviceThrowsUpsertException() throws Exception {
        OfficerDisqualification officer = createOfficer();
        when(disqualifiedSearchUpsertRequest.buildRequest(officer)).thenThrow(new IOException());

        Exception e = assertThrows(UpsertException.class,
                () -> service.createUpdateRequest(officer, OFFICER_ID));

        assertEquals("Unable to create update request", e.getMessage());
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