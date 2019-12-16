package uk.gov.companieshouse.search.api.service.search;

import org.elasticsearch.action.search.SearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchRequestService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlphabeticalSearchRequestServiceTest {

    @InjectMocks
    private SearchRequestService searchRequestService = new AlphabeticalSearchRequestService();

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    private static final String ENV_READER_RESULT = "1";
    private static final String SEARCH_PARAM = "search param";

    @Test
    @DisplayName("Test alphabetical search request created")
    public void testAlphabeticalSearchRequestCreated() {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT);
        SearchRequest searchRequest = searchRequestService.createSearchRequest(SEARCH_PARAM);
        assertNotNull(searchRequest);
    }
}
