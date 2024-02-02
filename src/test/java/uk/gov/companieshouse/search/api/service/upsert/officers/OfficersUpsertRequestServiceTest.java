package uk.gov.companieshouse.search.api.service.upsert.officers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.NoSuchElementException;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class OfficersUpsertRequestServiceTest {

    private static final String UPDATE_JSON = "{\"active_count\":0,\"inactive_count\":0,\"kind\":\"searchresults#officer\",\"resigned_count\":0,\"sort_key\":\"sort key\"}";
    private static final String OFFICER_ID = "testid";
    private static final String INDEX = "PRIMARY_SEARCH_INDEX";
    private static final String PRIMARY = "primary_search2";

    @Mock
    private ConversionService converter;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private ConfiguredIndexNamesProvider indices;
    private OfficersUpsertRequestService service;
    @Mock
    private AppointmentList appointmentList;

    private final OfficerSearchDocument officerSearchDocument = OfficerSearchDocument.Builder.builder()
            .sortKey("sort key")
            .build();

    @BeforeEach
    void setUp() {
        service = new OfficersUpsertRequestService(converter, mapper, indices);
    }

    @Test
    void serviceCreatesUpdateRequest() throws Exception {
        when(indices.primary()).thenReturn(PRIMARY);
        when(converter.convert(any(), eq(OfficerSearchDocument.class))).thenReturn(officerSearchDocument);
        when(mapper.writeValueAsString(any())).thenReturn(UPDATE_JSON);

        UpdateRequest request = service.createUpdateRequest(appointmentList, OFFICER_ID);

        assertEquals(OFFICER_ID, request.id());
        String expected = "update {[primary_search2][primary_search][" + OFFICER_ID
                + "], doc_as_upsert[true], doc[index {[null][_doc][null], source[" + UPDATE_JSON
                + "]}], scripted_upsert[false], detect_noop[true]}";
        assertEquals(expected, request.toString());
    }

    @Test
    void serviceThrowsNoSuchElementException() {
        when(indices.primary()).thenReturn(PRIMARY);

        assertThrows(NoSuchElementException.class, () -> service.createUpdateRequest(appointmentList, OFFICER_ID));
    }

    @Test
    void serviceCatchesIOException() throws Exception {
        when(indices.primary()).thenReturn(PRIMARY);
        when(converter.convert(any(), eq(OfficerSearchDocument.class))).thenReturn(officerSearchDocument);
        when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        Executable executable = () -> service.createUpdateRequest(appointmentList, OFFICER_ID);

        assertThrows(UpsertException.class, executable);
    }
}
