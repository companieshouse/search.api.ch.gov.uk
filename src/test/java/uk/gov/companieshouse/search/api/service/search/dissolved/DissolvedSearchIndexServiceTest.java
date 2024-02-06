package uk.gov.companieshouse.search.api.service.search.dissolved;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchRequestService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class DissolvedSearchIndexServiceTest {

    @InjectMocks
    private DissolvedSearchIndexService searchIndexService;

    @Mock
    private DissolvedSearchRequestService mockDissolvedSearchRequestService;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    private static final String REQUEST_ID = "requestId";
    private static final String COMPANY_NAME = "companyName";
    private static final String COMPANY_NUMBER = "companyName";
    private static final String COMPANY_STATUS = "companyStatus";
    private static final LocalDate DATE_OF_CESSATION = LocalDate.of(1993, 01, 01);
    private static final LocalDate DATE_OF_CREATION = LocalDate.of(1983, 01, 01);
    private static final LocalDate DATE_OF_NAME_CESSATION = LocalDate.of(1963, 01, 01);
    private static final LocalDate DATE_OF_NAME_EFFECTIVENESS = LocalDate.of(1973, 01, 01);
    private static final String LOCALITY = "locality";
    private static final String POSTAL_CODE = "AB12 C34";
    private static final String PREVIOUS_NAME = "previousName";
    private static final String KIND = "searchresults#dissolvedCompany";
    private static final String SEARCH_TYPE_BEST_MATCH = "best-match";
    private static final String SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH = "previous-name-dissolved";
    private static final Integer START_INDEX = 0;
    private static final String SEARCH_BEFORE = null;
    private static final String SEARCH_AFTER = null;
    private static final Integer SIZE = 20;

    @Test
    @DisplayName("Test dissolved alphabetical search request returns successfully")
    void searchDissolvedAlphabeticalRequestSuccessful() throws Exception {
        when(mockDissolvedSearchRequestService.getSearchResults(COMPANY_NAME, SEARCH_BEFORE, SEARCH_AFTER, SIZE,
                REQUEST_ID)).thenReturn(createSearchResults(true, false));
        ResponseObject responseObject = searchIndexService.searchAlphabetical(COMPANY_NAME, SEARCH_BEFORE,
                SEARCH_AFTER, SIZE, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test dissolved alphabetical search returns an error")
    void searchDissolvedAlphabeticalRequestReturnsError() throws Exception {
        when(mockDissolvedSearchRequestService.getSearchResults(COMPANY_NAME, SEARCH_BEFORE, SEARCH_AFTER, SIZE,
                REQUEST_ID)).thenThrow(SearchException.class);

        ResponseObject responseObject = searchIndexService.searchAlphabetical(COMPANY_NAME, SEARCH_BEFORE,
                SEARCH_AFTER, SIZE, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test dissolved alphabetical search returns no results")
    void searchDissolvedAlphabeticalRequestReturnsNoResults() throws Exception {
        when(mockDissolvedSearchRequestService.getSearchResults(COMPANY_NAME, null, null, null, REQUEST_ID))
                .thenReturn(createSearchResults(false, false));
        ResponseObject responseObject = searchIndexService.searchAlphabetical(COMPANY_NAME, SEARCH_BEFORE,
                SEARCH_AFTER, null, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test dissolved alphabetical search returns no results when response object is empty")
    void emptySearchDissolvedAlphabeticalRequestReturnsNoResults() throws Exception {
        when(mockDissolvedSearchRequestService.getSearchResults(COMPANY_NAME, null, null, null, REQUEST_ID))
            .thenReturn(createSearchResults(false, true));
        ResponseObject responseObject = searchIndexService.searchAlphabetical(COMPANY_NAME, SEARCH_BEFORE,
            SEARCH_AFTER, null, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test best match dissolved search request returns successfully")
    void searchBestMatchDissolvedRequestSuccessful() throws Exception {
        when(mockDissolvedSearchRequestService.getBestMatchSearchResults(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE)).thenReturn(createSearchResults(true, false));
        ResponseObject responseObject = searchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test best match dissolved search returns an error")
    void searchBestMatchDissolvedRequestReturnsError() throws Exception {
        when(mockDissolvedSearchRequestService.getBestMatchSearchResults(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE)).thenThrow(SearchException.class);

        ResponseObject responseObject = searchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test best match dissolved search returns no results")
    void searchBestMatchDissolvedRequestReturnsNoResults() throws Exception {
        when(mockDissolvedSearchRequestService.getBestMatchSearchResults(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE)).thenReturn(createSearchResults(false, false));
        ResponseObject responseObject = searchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    // PREVIOUS
    @Test
    @DisplayName("Test best match for previous company names on a dissolved search request returns successfully")
    void searchBestMatchPreviousNamesDissolvedRequestSuccessful() throws Exception {
        when(mockDissolvedSearchRequestService.getPreviousNamesResults(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE)).thenReturn(createSearchResults(true, false));
        ResponseObject responseObject = searchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test best match for previous company names on a dissolved search returns an error")
    void searchBestMatchPreviousNamesDissolvedRequestReturnsError() throws Exception {
        when(mockDissolvedSearchRequestService.getPreviousNamesResults(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE)).thenThrow(SearchException.class);

        ResponseObject responseObject = searchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test best match for previous company names on a dissolved search returns no results")
    void searchBestMatchPreviousNamesDissolvedRequestReturnsNoResults() throws Exception {
        when(mockDissolvedSearchRequestService.getPreviousNamesResults(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE)).thenReturn(createSearchResults(false, false));
        ResponseObject responseObject = searchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID,
                SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test best match for previous company names on a dissolved search returns no results when result is empty")
    void emptySearchBestMatchPreviousNamesDissolvedRequestReturnsNoResults() throws Exception {
        when(mockDissolvedSearchRequestService.getPreviousNamesResults(COMPANY_NAME, REQUEST_ID,
            SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE)).thenReturn(createSearchResults(false, true));
        ResponseObject responseObject = searchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID,
            SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }

    private SearchResults createSearchResults(boolean isResultsPopulated, boolean isItemsEmpty) {
        SearchResults searchResults = new SearchResults();
        TopHit topHit = new TopHit();
        topHit.setCompanyName("companyName");
        topHit.setCompanyNumber("00000000");
        searchResults.setTopHit(topHit);
        searchResults.setEtag("etag");
        searchResults.setKind(KIND);
        if (isResultsPopulated) {
            if(!isItemsEmpty) {
                searchResults.setItems(createItems());
            } else {
                searchResults.setItems(new ArrayList<Company>());
            }
        }
        return searchResults;
    }

    private List<Company> createItems() {
        List<Company> items = new ArrayList<>();
        Company dissolvedCompany = new Company();
        Address address = new Address();
        address.setLocality(LOCALITY);
        address.setPostalCode(POSTAL_CODE);
        PreviousCompanyName previousCompanyName = new PreviousCompanyName();
        previousCompanyName.setDateOfNameCessation(DATE_OF_NAME_CESSATION);
        previousCompanyName.setDateOfNameEffectiveness(DATE_OF_NAME_EFFECTIVENESS);
        previousCompanyName.setName(PREVIOUS_NAME);
        List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
        previousCompanyNames.add(previousCompanyName);
        dissolvedCompany.setCompanyName(COMPANY_NAME);
        dissolvedCompany.setCompanyNumber(COMPANY_NUMBER);
        dissolvedCompany.setCompanyStatus(COMPANY_STATUS);
        dissolvedCompany.setDateOfCessation(DATE_OF_CESSATION);
        dissolvedCompany.setDateOfCreation(DATE_OF_CREATION);
        dissolvedCompany.setRegisteredOfficeAddress(address);
        dissolvedCompany.setPreviousCompanyNames(previousCompanyNames);
        items.add(dissolvedCompany);
        return items;
    }
}
