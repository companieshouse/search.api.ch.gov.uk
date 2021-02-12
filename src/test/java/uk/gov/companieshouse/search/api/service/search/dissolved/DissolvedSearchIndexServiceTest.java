package uk.gov.companieshouse.search.api.service.search.dissolved;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DissolvedSearchIndexServiceTest {

    @InjectMocks
    DissolvedSearchIndexService searchIndexService;

    private static final String REQUEST_ID = "requestId";
    private static final String COMPANY_NAME = "test company";

    @Test
    @DisplayName("Test search request returns successfully")
    void searchRequestSuccessful() throws Exception {

        DissolvedResponseObject responseObject = searchIndexService.search(COMPANY_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.SEARCH_FOUND);
    }
}
