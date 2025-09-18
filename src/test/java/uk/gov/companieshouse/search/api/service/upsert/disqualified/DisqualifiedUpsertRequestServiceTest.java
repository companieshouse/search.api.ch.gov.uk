package uk.gov.companieshouse.search.api.service.upsert.disqualified;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.naming.ServiceUnavailableException;
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
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
 class DisqualifiedUpsertRequestServiceTest {

    private static final String UPDATE_JSON = "{\"example\":\"test\"}";
    private static final String OFFICER_ID = "testid";
    private static final String PRIMARY = "primary_search2";

    @Mock
    private DisqualifiedSearchUpsertRequest disqualifiedSearchUpsertRequest;
    @Mock
    private AlphaKeyService alphaKeyService;
    @Mock
    private ConfiguredIndexNamesProvider indices;
    @InjectMocks
    private DisqualifiedUpsertRequestService service;

    @Test
    void serviceCreatesUpdateRequest() throws Exception {
        OfficerDisqualification officer = createOfficer(true);
        when(indices.primary()).thenReturn(PRIMARY);
        when(disqualifiedSearchUpsertRequest.buildRequest(officer)).thenReturn(UPDATE_JSON);

        UpdateRequest request = service.createUpdateRequest(officer, OFFICER_ID);

        assertEquals(OFFICER_ID, request.id());
        String expected = "update {[primary_search2][primary_search][" + OFFICER_ID +
                "], doc_as_upsert[true], doc[index {[null][_doc][null], source[" + UPDATE_JSON +
                "]}], scripted_upsert[false], detect_noop[true]}";
        assertEquals(expected, request.toString());
    }

    @Test
    void serviceThrowsUpsertException() throws Exception {
        OfficerDisqualification officer = createOfficer(true);
        when(indices.primary()).thenReturn(PRIMARY);
        when(disqualifiedSearchUpsertRequest.buildRequest(officer)).thenThrow(new UpsertException(""));

        Exception e = assertThrows(UpsertException.class,
                () -> service.createUpdateRequest(officer, OFFICER_ID));

        assertEquals("Unable to create update request", e.getMessage());
    }

    @Test
    void serviceWithCorporateCreatesUpdateRequest() throws Exception {
        OfficerDisqualification officer = createOfficer(false);
        when(indices.primary()).thenReturn(PRIMARY);
        when(disqualifiedSearchUpsertRequest.buildRequest(officer)).thenReturn(UPDATE_JSON);
        AlphaKeyResponse response = new AlphaKeyResponse();
        response.setOrderedAlphaKey("abc");
        when(alphaKeyService.getAlphaKeyForCorporateName(officer.getItems().get(0).getCorporateName())).thenReturn(response);

        UpdateRequest request = service.createUpdateRequest(officer, OFFICER_ID);

        assertEquals(OFFICER_ID, request.id());
        String expected = "update {[primary_search2][primary_search][" + OFFICER_ID +
                "], doc_as_upsert[true], doc[index {[null][_doc][null], source[" + UPDATE_JSON +
                "]}], scripted_upsert[false], detect_noop[true]}";
        assertEquals(expected, request.toString());
        verify(alphaKeyService).getAlphaKeyForCorporateName(officer.getItems().get(0).getCorporateName());
    }

    @Test
    void alphaKeyFailThrowsServiceUnavailableException() throws Exception {
        OfficerDisqualification officer = createOfficer(false);
        when(indices.primary()).thenReturn(PRIMARY);

        Exception e = assertThrows(ServiceUnavailableException.class,
                () -> service.createUpdateRequest(officer, OFFICER_ID));

        assertEquals("Unable to create ordered alpha key", e.getMessage());
    }

    private OfficerDisqualification createOfficer(boolean natural) {
        OfficerDisqualification officer = new OfficerDisqualification();
        Item item = new Item();
        if (natural) {
            officer.setSortKey("abc");
        }
        item.setForename("Forename");
        item.setSurname("Surname");
        officer.addItemsItem(item);
        return officer;
    }
}
