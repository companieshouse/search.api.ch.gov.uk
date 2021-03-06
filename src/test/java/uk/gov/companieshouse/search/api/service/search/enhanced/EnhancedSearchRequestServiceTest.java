package uk.gov.companieshouse.search.api.service.search.enhanced;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.service.search.impl.enhanced.EnhancedSearchRequestService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnhancedSearchRequestServiceTest {

    @InjectMocks
    private EnhancedSearchRequestService searchRequestService;

    private static final String COMPANY_NAME = "test company";

    @Test
    @DisplayName("Test enhanced search returns results successfully")
    void testEnhancedSearch() {

        SearchResults<Company> searchResults =
                searchRequestService.getSearchResults(COMPANY_NAME);

        assertNotNull(searchResults);
        assertEquals(COMPANY_NAME, searchResults.getTopHit().getCompanyName());
        assertEquals("enhanced-search", searchResults.getKind());
    }

}
