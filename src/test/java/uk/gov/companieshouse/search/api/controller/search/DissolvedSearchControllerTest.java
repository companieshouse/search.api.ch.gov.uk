package uk.gov.companieshouse.search.api.controller.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DissolvedSearchControllerTest {

    @Mock
    private DissolvedSearchIndexService mockSearchIndexService;

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @InjectMocks
    private DissolvedSearchController dissolvedSearchController;

    private static final String REQUEST_ID = "requestID";
    private static final String COMPANY_NAME = "test company";
    private static final String SEARCH_TYPE_ALPHABETICAL = "alphabetical";
    private static final String SEARCH_TYPE_BEST_MATCH = "best-match";
    private static final String SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH = "previous-name-dissolved";
    private static final String COMPANY_NUMBER = "00000000";

    @Test
    @DisplayName("Test alphabetical search for dissolved found")
    void testAlphabeticalSearchForDissolvedFound() {

        DissolvedResponseObject responseObject =
                new DissolvedResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.searchAlphabetical(COMPANY_NAME, REQUEST_ID)).thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));

        ResponseEntity responseEntity =
                dissolvedSearchController.searchCompanies(COMPANY_NAME, SEARCH_TYPE_ALPHABETICAL, null, null, null, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test best match for dissolved found")
    void testBestMatchForDissolvedFound() {

        DissolvedResponseObject responseObject =
                new DissolvedResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_BEST_MATCH)).thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));

        ResponseEntity responseEntity =
                dissolvedSearchController.searchCompanies(COMPANY_NAME, SEARCH_TYPE_BEST_MATCH, null, null, null, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test previous names best match for dissolved found")
    void testPreviousNamesBestMatchForDissolvedFound() {

        DissolvedResponseObject responseObject =
                new DissolvedResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH)).thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));

        ResponseEntity responseEntity =
                dissolvedSearchController.searchCompanies(COMPANY_NAME, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, null, null, null, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test incorrect search_type parameter returns correct response")
    void testIncorrectSearchTypeParameter() {

        when(mockApiToResponseMapper.mapDissolved(any()))
                .thenReturn(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Invalid url parameter for search_type, please try 'alphabetical' or 'best-match'"));

        ResponseEntity responseEntity =
                dissolvedSearchController.searchCompanies(COMPANY_NAME, "aaa", null, null, null, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private DissolvedSearchResults createSearchResults() {
        DissolvedSearchResults<DissolvedCompany> searchResults = new DissolvedSearchResults<>();
        List<DissolvedCompany> companies = new ArrayList<>();
        DissolvedCompany company = new DissolvedCompany();

        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyStatus(COMPANY_NUMBER);
        companies.add(company);

        TopHit topHit = new TopHit();
        topHit.setCompanyNumber("00004444");
        topHit.setCompanyName(COMPANY_NAME);

        searchResults.setItems(companies);
        searchResults.setEtag("test etag");
        searchResults.setTopHit(topHit);


        return searchResults;
    }
}
