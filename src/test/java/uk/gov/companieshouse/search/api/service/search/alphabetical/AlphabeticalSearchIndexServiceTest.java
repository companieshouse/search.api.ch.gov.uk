package uk.gov.companieshouse.search.api.service.search.alphabetical;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlphabeticalSearchIndexServiceTest {

    @InjectMocks
    private SearchIndexService searchIndexService = new AlphabeticalSearchIndexService();

    @Mock
    private SearchRequestService mockSearchRequestService;

    private static final String TOP_HIT = "AAAA COMMUNICATIONS LIMITED";
    private static final String REQUEST_ID = "requestId";
    private static final String CORPORATE_NAME = "corporateName";

    @Test
    @DisplayName("Test search request returns successfully")
    void searchRequestSuccessful() throws Exception {
        when(mockSearchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, REQUEST_ID))
            .thenReturn(createSearchResults(true));
        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns an error")
    void searchRequestReturnsError() throws Exception {
        when(mockSearchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, REQUEST_ID))
            .thenThrow(SearchException.class);

        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns no results")
    void searchRequestReturnsNoResults() throws Exception {
        when(mockSearchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, REQUEST_ID))
            .thenReturn(createSearchResults(false));
        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    private SearchResults createSearchResults(boolean isResultsPopulated) {
        SearchResults searchResults = new SearchResults();
        searchResults.setSearchType("alphabetical");
        searchResults.setTopHit(TOP_HIT);

        if (isResultsPopulated) {
            searchResults.setResults(createResults());
        }
        return searchResults;
    }

    private List<Company> createResults() {
        List<Company> results = new ArrayList<>();
        Company company = new Company();
        Items items = new Items();
        Links links = new Links();

        items.setCorporateName("corporateName");
        items.setCompanyStatus("companyStatus");
        items.setCompanyNumber("companyNumber");
        items.setRecordType("recordType");

        links.setSelf("self");

        company.setId("id");
        company.setCompanyType("companyType");
        company.setItems(items);
        company.setLinks(links);
        results.add(company);

        return results;
    }
}
