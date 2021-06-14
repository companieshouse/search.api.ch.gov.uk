package uk.gov.companieshouse.search.api.controller.search;

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
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_NOT_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SIZE_PARAMETER_ERROR;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlphabeticalSearchControllerTest {

    @Mock
    private AlphabeticalSearchIndexService mockSearchIndexService;

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @InjectMocks
    private AlphabeticalSearchController alphabeticalSearchController;

    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;

    private static final String REQUEST_ID = "requestID";
    private static final String COMPANY_NAME = "test name";
    private static final String MAX_SIZE_PARAM = "MAX_SIZE_PARAM";

    @Test
    @DisplayName("Test search not found")
    void testSearchNotFound() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_NOT_FOUND, null);

        when(mockSearchIndexService.search(COMPANY_NAME, null, null, 20, REQUEST_ID)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(NOT_FOUND).build());
        doReturn(50).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);

        ResponseEntity<?> responseEntity =
            alphabeticalSearchController.searchByCorporateName("test name", null, null, 20, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search found")
     void testSearchFound() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_FOUND, createSearchResults());

        when(mockSearchIndexService.search(COMPANY_NAME, null, null, 20, REQUEST_ID)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));
        doReturn(50).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);

        ResponseEntity<?> responseEntity =
            alphabeticalSearchController.searchByCorporateName("test name", null, null, 20, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search invalid as size parameter is greater than max allowed")
    void testInvalidSizeParameter() {

        ResponseEntity responseEntity = getResponseEntity(101);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search invalid as size parameter is less than 0")
    void testNegativeSizeParameter() {

        ResponseEntity responseEntity = getResponseEntity(-6);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search invalid as size parameter is 0")
    void testZeroSizeParameter() {

        ResponseEntity responseEntity = getResponseEntity(0);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search invalid as size parameter is null")
    void testNullSizeParameter() {

        ResponseEntity responseEntity = getResponseEntity(null);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    private SearchResults<Company> createSearchResults() {
        SearchResults<Company> searchResults = new SearchResults<>();
        List<Company> companies = new ArrayList<>();
        
        Company company = new Company();
        
        company.setCompanyNumber("00004444");
        company.setCompanyName("test name");
        company.setCompanyStatus("test status");
        companies.add(company);

        searchResults.setItems(companies);
        searchResults.setKind("test search type");

        return searchResults;
    }

    private ResponseEntity<?> getResponseEntity(Integer size) {
        doReturn(50).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
            .thenReturn(ResponseEntity.status(UNPROCESSABLE_ENTITY).build());

       return alphabeticalSearchController.searchByCorporateName("test name", null, null, size, REQUEST_ID);
    }
}
