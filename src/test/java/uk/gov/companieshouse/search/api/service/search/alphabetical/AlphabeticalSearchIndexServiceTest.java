package uk.gov.companieshouse.search.api.service.search.alphabetical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class AlphabeticalSearchIndexServiceTest {

    @InjectMocks
    private AlphabeticalSearchIndexService searchIndexService;

    @Mock
    private SearchRequestService mockSearchRequestService;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    private TopHit TOP_HIT;
    private static final String REQUEST_ID = "requestId";
    private static final String CORPORATE_NAME = "corporateName";

    @BeforeEach
    void setUp() {
        TOP_HIT = new TopHit();
        TOP_HIT.setCompanyName("AAAA COMMUNICATIONS LIMITED");
    }

    @Test
    @DisplayName("Test search request returns successfully")
    void searchRequestSuccessful() throws Exception {
        when(mockSearchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, null, REQUEST_ID))
            .thenReturn(createSearchResults(true, false));
        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, null, null, null, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns an error")
    void searchRequestReturnsError() throws Exception {
        when(mockSearchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, null, REQUEST_ID))
            .thenThrow(SearchException.class);

        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, null, null, null, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns no results")
    void searchRequestReturnsNoResults() throws Exception {
        when(mockSearchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, null, REQUEST_ID))
            .thenReturn(createSearchResults(false, false));
        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, null, null, null, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns empty results")
    void emptySearchRequestReturnsNoResults() throws Exception {
        when(mockSearchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, null, REQUEST_ID))
            .thenReturn(createSearchResults(false, true));
        ResponseObject responseObject = searchIndexService.search(CORPORATE_NAME, null, null, null, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    private SearchResults<Company> createSearchResults(boolean isResultsPopulated, boolean isItemsEmpty) {
        SearchResults<Company> searchResults = new SearchResults<>();
        searchResults.setKind("alphabetical");

        if (!isItemsEmpty) {
            searchResults.setTopHit(TOP_HIT);

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

        //company.setId("id");
        company.setCompanyType("companyType");
        company.setLinks(links);
        results.add(company);

        return results;
    }
}
