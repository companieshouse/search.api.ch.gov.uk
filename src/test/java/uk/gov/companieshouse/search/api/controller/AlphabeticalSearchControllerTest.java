package uk.gov.companieshouse.search.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SIZE_PARAMETER_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_DELETED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_NOT_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DELETE_NOT_FOUND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.delete.alphabetical.AlphabeticalSearchDeleteService;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class AlphabeticalSearchControllerTest {

    @Mock
    private AlphabeticalSearchIndexService mockSearchIndexService;
    
    @Mock
    private UpsertCompanyService mockUpsertCompanyService;

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;
    
    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    @InjectMocks
    private AlphabeticalSearchController alphabeticalSearchController;

    @Mock
    private AlphabeticalSearchDeleteService alphabeticalSearchDeleteService;

    private static final String REQUEST_ID = "requestID";
    private static final String COMPANY_NAME = "test name";
    private static final String MAX_SIZE_PARAM = "MAX_SIZE_PARAM";
    private static final String ALPHABETICAL_SEARCH_RESULT_MAX = "ALPHABETICAL_SEARCH_RESULT_MAX";

    @Test
    @DisplayName("Test search not found")
    void testSearchNotFound() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_NOT_FOUND, null);

        when(mockSearchIndexService.search(COMPANY_NAME, null, null, 20, REQUEST_ID)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(NOT_FOUND).build());
        doReturn(50).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ALPHABETICAL_SEARCH_RESULT_MAX);

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
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ALPHABETICAL_SEARCH_RESULT_MAX);

        ResponseEntity<?> responseEntity =
            alphabeticalSearchController.searchByCorporateName("test name", null, null, 20, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search size set to default if size parameter is null")
    void testNullSizeParameter() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_FOUND, createSearchResults());

        doReturn(50).when(mockEnvironmentReader).getMandatoryInteger(MAX_SIZE_PARAM);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ALPHABETICAL_SEARCH_RESULT_MAX);

        when(mockSearchIndexService.search(COMPANY_NAME, null, null, 20, REQUEST_ID)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));

        ResponseEntity<?> responseEntity =
            alphabeticalSearchController.searchByCorporateName("test name", null, null, null, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }
    
    @Test
    @DisplayName("Test upsert company is successful")
    void testUpsertSuccessful() {
        ResponseObject responseObject = new ResponseObject(DOCUMENT_UPSERTED);
        CompanyProfileApi company = createCompany();

        when(mockUpsertCompanyService.upsert(company)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject)).thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = alphabeticalSearchController.upsertCompany(company.getCompanyNumber(), company);

        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test upsert company failed to index or update document")
    void testUpsertFailedToIndexOrUpdate() {
        ResponseObject responseObject = new ResponseObject(UPSERT_ERROR);
        CompanyProfileApi company = createCompany();

        when(mockUpsertCompanyService.upsert(company)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
                .thenReturn(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());

        ResponseEntity<?> responseEntity = alphabeticalSearchController.upsertCompany(company.getCompanyNumber(), company);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test upsert failed during update request")
    void testUpsertFailedUpdateRequest() {
        ResponseObject responseObject = new ResponseObject(UPDATE_REQUEST_ERROR);
        CompanyProfileApi company = createCompany();

        when(mockUpsertCompanyService.upsert(company)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
                .thenReturn(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());

        ResponseEntity<?> responseEntity = alphabeticalSearchController.upsertCompany(company.getCompanyNumber(), company);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @ParameterizedTest(name = "Upsert with companyNumber={0} should return HTTP 400 Bad Request")
    @NullSource
    @ValueSource(strings = {"", "1234567890"})
    void testUpsertWithInvalidCompanyNumberReturnsBadRequest(String companyNumber) {
        CompanyProfileApi company = createCompany();

        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = alphabeticalSearchController.upsertCompany(companyNumber, company);

        assertEquals(UPSERT_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search invalid as size parameter is greater than max allowed")
    void testInvalidSizeParameter() {

        ResponseEntity<?> responseEntity = getResponseEntity(101);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search invalid as size parameter is less than 0")
    void testNegativeSizeParameter() {

        ResponseEntity<?> responseEntity = getResponseEntity(-6);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test search invalid as size parameter is 0")
    void testZeroSizeParameter() {

        ResponseEntity<?> responseEntity = getResponseEntity(0);

        assertEquals(SIZE_PARAMETER_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns a HTTP 200 Ok Response if company is found in the index")
    void testDeleteWithCompanyNumberReturnsOkRequest() {

        String companyNumber = "00002400";

        when(alphabeticalSearchDeleteService.deleteCompany(companyNumber))
                .thenReturn(new ResponseObject(DOCUMENT_DELETED));
        when(mockApiToResponseMapper.map(any()))
                .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = alphabeticalSearchController.deleteCompany(companyNumber);

        verify(mockApiToResponseMapper).map(responseObjectCaptor.capture());
        assertEquals(DOCUMENT_DELETED, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns a HTTP 404 Response if the company number is not presented")
    void testDeleteWithMissingCompanyNumberReturnsNotFound() {
        when(mockApiToResponseMapper.map(any()))
                .thenReturn(ResponseEntity.status(NOT_FOUND).build());

        ResponseEntity<?> responseEntity = alphabeticalSearchController.deleteCompany("");

        verify(mockApiToResponseMapper).map(responseObjectCaptor.capture());
        assertEquals(DELETE_NOT_FOUND, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns a HTTP 400 BAD REQUEST Response if the company number is missing")
    void testDeleteWithEmptyCompanyNumberReturnsNotFound() {

        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = alphabeticalSearchController.deleteCompany(null);

        assertEquals(DELETE_NOT_FOUND, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    private CompanyProfileApi createCompany() {
        CompanyProfileApi company = new CompanyProfileApi();
        company.setType("company type");
        company.setCompanyNumber("company number");
        company.setCompanyStatus("company status");
        company.setCompanyName("company name");

        Map<String, String> links = new HashMap<>();
        links.put("self", "company/00000000");
        company.setLinks(links);

        return company;
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
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ALPHABETICAL_SEARCH_RESULT_MAX);
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
            .thenReturn(ResponseEntity.status(UNPROCESSABLE_ENTITY).build());

       return alphabeticalSearchController.searchByCorporateName("test name", null, null, size, REQUEST_ID);
    }
}
