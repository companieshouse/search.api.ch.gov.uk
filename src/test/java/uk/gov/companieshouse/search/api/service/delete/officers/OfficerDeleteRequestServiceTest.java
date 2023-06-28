package uk.gov.companieshouse.search.api.service.delete.officers;

import org.elasticsearch.action.delete.DeleteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.service.delete.disqualified.DisqualifiedDeleteRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfficerDeleteRequestServiceTest {
    private final String INDEX = "primary_search2";
    private final String TYPE = "primary_search";
    private final String OFFICER_ID = "officerId";
    @Mock
    EnvironmentReader reader;

    @InjectMocks
    DisqualifiedDeleteRequestService service;

    @BeforeEach
    void setup() {
        when(reader.getMandatoryString("PRIMARY_SEARCH_INDEX")).thenReturn(INDEX);
    }
    @Test
    @DisplayName("Create request for deleting an officer from the primary search index")
    void createsDeleteRequest() {
        DeleteRequest request = service.createDeleteRequest(OFFICER_ID);

        assertEquals(OFFICER_ID, request.id());
        assertEquals(INDEX, request.index());
        assertEquals(TYPE, request.type());
    }
}
