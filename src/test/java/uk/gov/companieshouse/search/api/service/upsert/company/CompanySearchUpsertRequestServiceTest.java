package uk.gov.companieshouse.search.api.service.upsert.company;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
public class CompanySearchUpsertRequestServiceTest {
    private static final String COMPANY_NUMBER = "12345678";
    private static final String PRIMARY = "primary_search2";

    @Mock
    private ConfiguredIndexNamesProvider indices;

    @Mock
    private CompanySearchUpsertRequestService service;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private Data profileData;
    @BeforeEach
    void setUp() {
        service = new CompanySearchUpsertRequestService(mapper, indices);
    }
    /*@Test
    void serviceThrowsNoSuchElementException() throws JsonProcessingException {
        when(indices.primary()).thenReturn(PRIMARY);

        assertThrows(NoSuchElementException.class, () -> service.createUpdateRequest(COMPANY_NUMBER, profileData));
    }*/

    @Test
    void serviceCatchesIOException() throws Exception {
        when(indices.primary()).thenReturn(PRIMARY);

        when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        Executable executable = () -> service.createUpdateRequest(COMPANY_NUMBER, profileData);

        assertThrows(UpsertException.class, executable);
    }
}
