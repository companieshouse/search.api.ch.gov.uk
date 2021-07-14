package uk.gov.companieshouse.search.api.service.search.enhanced;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.impl.enhanced.EnhancedSearchIndexService;
import uk.gov.companieshouse.search.api.service.search.impl.enhanced.EnhancedSearchRequestService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnhancedSearchIndexServiceTest {

    @InjectMocks
    private EnhancedSearchIndexService searchIndexService;

    @Mock
    private EnhancedSearchRequestService mockEnhancedSearchRequestService;

    private static final String COMPANY_NAME = "test company";

    @Test
    @DisplayName("Test enhanced search request returns successfully")
    void searchRequestSuccessful() throws Exception {
        when(mockEnhancedSearchRequestService.getSearchResults(COMPANY_NAME))
                .thenReturn(createSearchResults(true, false));;
        ResponseObject responseObject = searchIndexService.searchEnhanced(COMPANY_NAME);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_FOUND, responseObject.getStatus());
    }

    private SearchResults<Company> createSearchResults(boolean isResultsPopulated, boolean isItemsEmpty) {
        SearchResults<Company> searchResults = new SearchResults<>();
        searchResults.setKind("enhanced");

        if (!isItemsEmpty) {
            searchResults.setTopHit(null);

            if (isResultsPopulated) {
                searchResults.setItems(createResults());
            }
        } else {
            searchResults.setTopHit(null);
            searchResults.setItems(null);
        }
        return searchResults;
    }

    private List<Company> createResults() {
        List<Company> results = new ArrayList<>();
        Company company = new Company();
        Links links = new Links();

        company.setCompanyName("corporateName");
        company.setCompanyStatus("companyStatus");
        company.setCompanyNumber("companyNumber");
        company.setRecordType("recordType");

        links.setCompanyProfile("self");

        company.setCompanyType("companyType");
        company.setLinks(links);
        results.add(company);

        return results;
    }
}