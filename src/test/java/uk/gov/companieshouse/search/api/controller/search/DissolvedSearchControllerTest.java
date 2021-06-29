package uk.gov.companieshouse.search.api.controller.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SIZE_PARAMETER_ERROR;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DissolvedSearchControllerTest {

    @Mock
    private DissolvedSearchIndexService mockSearchIndexService;

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;

    @InjectMocks
    private DissolvedSearchController dissolvedSearchController;

    private static final String MAX_SIZE_PARAM = "MAX_SIZE_PARAM";
    private static final String DISSOLVED_ALPHABETICAL_SEARCH_RESULT_MAX = "DISSOLVED_ALPHABETICAL_SEARCH_RESULT_MAX";
    private static final String DISSOLVED_SEARCH_RESULT_MAX = "DISSOLVED_SEARCH_RESULT_MAX";
    private static final String REQUEST_ID = "requestID";
    private static final String COMPANY_NAME = "test company";
    private static final String SEARCH_TYPE_ALPHABETICAL = "alphabetical";
    private static final String SEARCH_TYPE_BEST_MATCH = "best-match";
    private static final String SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH = "previous-name-dissolved";
    private static final String COMPANY_NUMBER = "00000000";
    private static final Integer START_INDEX = 0;
    private static final String SEARCH_BEFORE = null;
    private static final String SEARCH_AFTER = null;
    private static final Integer SIZE = 20;

    @Test
    @DisplayName("Test alphabetical search for dissolved found")
    void testAlphabeticalSearchForDissolvedFound() {

        ResponseObject responseObject = new ResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.searchAlphabetical(COMPANY_NAME, SEARCH_BEFORE, SEARCH_AFTER, SIZE, REQUEST_ID))
                .thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));
        doReturn(100).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(40).when(mockEnvironmentReader).getMandatoryInteger(DISSOLVED_ALPHABETICAL_SEARCH_RESULT_MAX);

        ResponseEntity<?> responseEntity = dissolvedSearchController.searchCompanies(COMPANY_NAME,
                SEARCH_TYPE_ALPHABETICAL, SEARCH_BEFORE, SEARCH_AFTER, SIZE, START_INDEX, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test best match for dissolved found")
    void testBestMatchForDissolvedFound() {

        ResponseObject responseObject = new ResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE))
                .thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));
        doReturn(100).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(DISSOLVED_SEARCH_RESULT_MAX);

        ResponseEntity<?> responseEntity = dissolvedSearchController.searchCompanies(COMPANY_NAME, SEARCH_TYPE_BEST_MATCH,
                SEARCH_BEFORE, SEARCH_AFTER, SIZE, START_INDEX, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test best match for dissolved found without providing start index")
    void testBestMatchForDissolvedFoundNoStartIndex() {

        ResponseObject responseObject = new ResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE))
                .thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));
        doReturn(100).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(DISSOLVED_SEARCH_RESULT_MAX);

        ResponseEntity<?> responseEntity = dissolvedSearchController.searchCompanies(COMPANY_NAME, SEARCH_TYPE_BEST_MATCH,
                SEARCH_BEFORE, SEARCH_AFTER, SIZE, null, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test best match for dissolved found with start index out of bounds")
    void testBestMatchForDissolvedFoundStartIndexOutOfBounds() {

        ResponseObject responseObject = new ResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE))
                .thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));
        doReturn(100).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(DISSOLVED_SEARCH_RESULT_MAX);

        ResponseEntity<?> responseEntity = dissolvedSearchController.searchCompanies(COMPANY_NAME, SEARCH_TYPE_BEST_MATCH,
                SEARCH_BEFORE, SEARCH_AFTER, SIZE, -1, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test previous names best match for dissolved found")
    void testPreviousNamesBestMatchForDissolvedFound() {

        ResponseObject responseObject = new ResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.searchBestMatch(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH,
                START_INDEX, SIZE)).thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));
        doReturn(100).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(DISSOLVED_SEARCH_RESULT_MAX);

        ResponseEntity<?> responseEntity = dissolvedSearchController.searchCompanies(COMPANY_NAME,
                SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, SEARCH_BEFORE, SEARCH_AFTER, SIZE, START_INDEX, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test incorrect search_type parameter returns correct response")
    void testIncorrectSearchTypeParameter() {

        when(mockApiToResponseMapper.mapDissolved(any())).thenReturn(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body("Invalid url parameter for search_type, please try 'alphabetical' or 'best-match'"));

        ResponseEntity<?> responseEntity = dissolvedSearchController.searchCompanies(COMPANY_NAME, "aaa", SEARCH_BEFORE,
                SEARCH_AFTER, SIZE, START_INDEX, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test Alphabetical dissolved search size set to default if size parameter is null")
    void testNullSizeParameter() {

        ResponseObject responseObject = new ResponseObject(SEARCH_FOUND, createSearchResults());

        doReturn(100).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(40).when(mockEnvironmentReader).getMandatoryInteger(DISSOLVED_ALPHABETICAL_SEARCH_RESULT_MAX);

        when(mockSearchIndexService.searchAlphabetical(COMPANY_NAME, SEARCH_BEFORE, SEARCH_AFTER, 40, REQUEST_ID))
            .thenReturn(responseObject);
        when(mockApiToResponseMapper.mapDissolved(responseObject))
            .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));


        ResponseEntity<?> responseEntity = dissolvedSearchController.searchCompanies(COMPANY_NAME,
            SEARCH_TYPE_ALPHABETICAL, SEARCH_BEFORE, SEARCH_AFTER, null, START_INDEX, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test Alphabetical dissolved search invalid as size parameter is greater than max allowed")
    void testInvalidSizeParameter() {

        ResponseEntity responseEntity = getResponseEntity(101);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test Alphabetical dissolved search invalid as size parameter is less than 0")
    void testNegativeSizeParameter() {


        ResponseEntity responseEntity = getResponseEntity(-6);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test Alphabetical dissolved search invalid as size parameter is 0")
    void testZeroSizeParameter() {

        ResponseEntity responseEntity = getResponseEntity(0);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    private SearchResults<?> createSearchResults() {
        SearchResults<Company> searchResults = new SearchResults<>();
        List<Company> companies = new ArrayList<>();
        Company company = new Company();

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

    private ResponseEntity<?> getResponseEntity(Integer size) {
        doReturn(50).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(40).when(mockEnvironmentReader).getMandatoryInteger(DISSOLVED_ALPHABETICAL_SEARCH_RESULT_MAX);

        when(mockApiToResponseMapper.mapDissolved(responseObjectCaptor.capture()))
            .thenReturn(ResponseEntity.status(UNPROCESSABLE_ENTITY).build());

        return dissolvedSearchController.searchCompanies("test name", "alphabetical", null, null, size, null, REQUEST_ID);
    }
}
