package uk.gov.companieshouse.search.api.service.delete.disqualified;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.elasticsearch.action.delete.DeleteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
public class DisqualifiedDeleteRequestServiceTest {

    private static final String INDEX = "primary_search2";
    private static final String TYPE = "primary_search";
    private static final String OFFICER_ID = "officerId";

    @Mock
    EnvironmentReader reader;

    @InjectMocks
    DisqualifiedDeleteRequestService service;

    @BeforeEach
    void setup() {
        when(reader.getMandatoryString("DISQUALIFIED_SEARCH_INDEX")).thenReturn(INDEX);
    }

    @Test
    void createsDeleteRequest() {
        DeleteRequest request = service.createDeleteRequest(OFFICER_ID);

        assertEquals(OFFICER_ID, request.id());
        assertEquals(INDEX, request.index());
        assertEquals(TYPE, request.type());
    }
}
