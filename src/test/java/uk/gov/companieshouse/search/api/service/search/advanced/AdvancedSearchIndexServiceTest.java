package uk.gov.companieshouse.search.api.service.search.advanced;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchIndexService;
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchRequestService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvancedSearchIndexServiceTest {

    @InjectMocks
    private AdvancedSearchIndexService searchIndexService;

    @Mock
    private AdvancedSearchRequestService mockAdvancedSearchRequestService;

    private static final String COMPANY_NAME = "test company";
    private static final String SIC_CODES = "99960";
    private static final List<String> SIC_CODES_LIST = Arrays.asList(SIC_CODES, "0000000");
    private static final String REQUEST_ID = "requestId";

    @Test
    @DisplayName("Test advanced search request returns successfully")
    void searchRequestSuccessful() throws Exception {

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME);
        advancedSearchQueryParams.setSicCodes(SIC_CODES_LIST);

        when(mockAdvancedSearchRequestService.getSearchResults(advancedSearchQueryParams, "request id"))
                .thenReturn(createSearchResults(true, false));;
        ResponseObject responseObject = searchIndexService.searchAdvanced(advancedSearchQueryParams, "request id");

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns an error")
    void searchRequestReturnsError() throws Exception {

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME);

        when(mockAdvancedSearchRequestService.getSearchResults(any(), anyString()))
                .thenThrow(SearchException.class);

        ResponseObject responseObject = searchIndexService.searchAdvanced(advancedSearchQueryParams, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns no results")
    void searchRequestReturnsNoResults() throws Exception {
        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME);

        when(mockAdvancedSearchRequestService.getSearchResults(any(), anyString()))
                .thenReturn(createSearchResults(false, false));
        ResponseObject responseObject = searchIndexService.searchAdvanced(advancedSearchQueryParams, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    private SearchResults<Company> createSearchResults(boolean isResultsPopulated, boolean isItemsEmpty) {
        SearchResults<Company> searchResults = new SearchResults<>();
        searchResults.setKind("advanced");

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
        company.setSicCodes(SIC_CODES_LIST);

        links.setCompanyProfile("self");

        company.setCompanyType("companyType");
        company.setLinks(links);
        results.add(company);

        return results;
    }
}