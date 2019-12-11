package uk.gov.companieshouse.search.api.service.search;

import org.elasticsearch.action.search.SearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchRequestService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlphabeticalSearchRequestServiceTest {

    @InjectMocks
    private SearchRequestService searchRequestService = new AlphabeticalSearchRequestService();

    @Test
    @DisplayName("Test alphabetical search request created")
    public void testAlphabeticalSearchRequestCreated() {

        SearchRequest searchRequest = searchRequestService.createSearchRequest("search param");
        assertNotNull(searchRequest);
    }
}
