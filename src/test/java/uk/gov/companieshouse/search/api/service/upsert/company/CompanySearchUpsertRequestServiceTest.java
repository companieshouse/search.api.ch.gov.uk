package uk.gov.companieshouse.search.api.service.upsert.company;

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
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchDocument;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class CompanySearchUpsertRequestServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    private static final String PRIMARY = "primary_search2";

    private static final String UPDATE_JSON = "{\"items\":[null],\"company_type\":plc,\"kind\":\"searchresults#company\",\"links\":links,\"sort_key\":\"sort key\"}";

    @Mock
    ConversionService companySearchDocumentConverter;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    @Mock
    private CompanySearchUpsertRequestService service;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private Data profileData;

    @Mock
    private CompanySearchDocument companySearchDocument;

    @BeforeEach
    void setUp() {
        service = new CompanySearchUpsertRequestService(companySearchDocumentConverter, mapper, indices);
    }

    @Test
    void serviceCreatesUpdateRequest() throws Exception {
        // given
        when(indices.primary()).thenReturn(PRIMARY);
        when(companySearchDocumentConverter.convert(any(),
                eq(CompanySearchDocument.class))).thenReturn(companySearchDocument);
        when(mapper.writeValueAsString(any())).thenReturn(UPDATE_JSON);

        // when
        UpdateRequest request = service.createUpdateRequest(COMPANY_NUMBER, profileData);

        // then
        assertEquals(COMPANY_NUMBER, request.id());
        String expected = "update {[primary_search2][primary_search][" + COMPANY_NUMBER
                + "], doc_as_upsert[true], doc[index {[null][_doc][null], source[" + UPDATE_JSON
                + "]}], scripted_upsert[false], detect_noop[true]}";
        assertEquals(expected, request.toString());
    }

    @Test
    void serviceThrowsNoSuchElementException() throws JsonProcessingException {
        // given

        // when
        when(indices.primary()).thenReturn(PRIMARY);

        // then
        assertThrows(NoSuchElementException.class, () -> service.createUpdateRequest(COMPANY_NUMBER, profileData));
    }

    @Test
    void serviceCatchesIOException() throws Exception {
        // given
        when(indices.primary()).thenReturn(PRIMARY);
        when(companySearchDocumentConverter.convert(any(), eq(CompanySearchDocument.class)))
                .thenReturn(companySearchDocument);
        when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        // when
        Executable executable = () -> service.createUpdateRequest(COMPANY_NUMBER, profileData);

        // then
        assertThrows(UpsertException.class, executable);
    }
}