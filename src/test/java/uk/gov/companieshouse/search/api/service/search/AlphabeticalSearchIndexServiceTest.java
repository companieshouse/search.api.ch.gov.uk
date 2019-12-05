package uk.gov.companieshouse.search.api.service.search;

import org.elasticsearch.action.search.SearchResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_ERROR;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlphabeticalSearchIndexServiceTest {

    @InjectMocks
    private SearchIndexService searchIndexService = new AlphabeticalSearchIndexService();

    @Mock
    private SearchRestClientService mockSearchRestClientService;

    @Test
    @DisplayName("Test search Error returned when no highest match found returned")
    public void testSearchFound() throws IOException {

        when(mockSearchRestClientService.searchRestClient(anyString())).thenReturn(any(SearchResponse.class));

        ResponseObject responseObject = searchIndexService.search("test param");

        assertNotNull(responseObject);
        assertEquals(SEARCH_ERROR,responseObject.getStatus());

    }
}
