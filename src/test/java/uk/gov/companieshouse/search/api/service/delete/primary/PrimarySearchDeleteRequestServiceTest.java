package uk.gov.companieshouse.search.api.service.delete.primary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.elasticsearch.action.delete.DeleteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.SearchType;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class PrimarySearchDeleteRequestServiceTest {

    private final String INDEX = "primary_search2";
    private final String TYPE = "primary_search";
    private final String OFFICER_ID = "officerId";
    private final String PRIMARY_SEARCH_TYPE = "disqualified-officer";

    @Mock
    private ConfiguredIndexNamesProvider indices;

    @InjectMocks
    PrimarySearchDeleteRequestService service;

    @BeforeEach
    void setup() {
        when(indices.primary()).thenReturn(INDEX);
    }

    @Test
    void createsDeleteRequest() {
        DeleteRequest request = service.createDeleteRequest(new SearchType(OFFICER_ID, PRIMARY_SEARCH_TYPE));

        assertEquals(OFFICER_ID, request.id());
        assertEquals(INDEX, request.index());
        assertEquals(TYPE, request.type());
    }
}
