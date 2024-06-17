package uk.gov.companieshouse.search.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;

import java.io.IOException;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DELETE_NOT_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_DELETED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SERVICE_UNAVAILABLE;


@ExtendWith(MockitoExtension.class)
class CompanySearchControllerTest {
    private final String COMPANY_NUMBER = "12345678";
    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;

    @InjectMocks
    private CompanySearchController companySearchController;

    @Mock
    private PrimarySearchDeleteService primarySearchDeleteService;

    @Mock
    private UpsertCompanyService upsertCompanyService;

    @Mock
    private Data profileData;

    @Test
    @DisplayName("Test upsert returns HTTP 200 OK given company exist in index")
    void testUpsertWithCorrectOfficerIdReturnsOkRequest() {
        when(upsertCompanyService.upsertCompany(anyString(), any()))
                .thenReturn(new ResponseObject(DOCUMENT_UPSERTED));
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = companySearchController.upsertCompanyPrimarySearch(COMPANY_NUMBER, profileData);

        assertEquals(DOCUMENT_UPSERTED, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }
    @Test
    @DisplayName("Test upsert returns HTTP 400 Bad Request when creating upsert request")
    void testUpsertCompanyReturns400BadRequestWhenCreatingUpsertRequest() {
        when(upsertCompanyService.upsertCompany(anyString(), any()))
                .thenReturn(new ResponseObject(UPSERT_ERROR));
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = companySearchController.upsertCompanyPrimarySearch(COMPANY_NUMBER, profileData);

        assertEquals(UPSERT_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test upsert returns HTTP 400 Bad Request when upserting to primary search")
    void testUpsertCompanyReturns400BadRequestWhenUpsertingToPrimarySearch() {
        when(upsertCompanyService.upsertCompany(anyString(), any()))
                .thenReturn(new ResponseObject(UPDATE_REQUEST_ERROR));
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = companySearchController.upsertCompanyPrimarySearch(COMPANY_NUMBER, profileData);

        assertEquals(UPDATE_REQUEST_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test upsert returns HTTP 503 Service Unavailable when upserting to primary search")
    void testUpsertCompanyReturns503BServiceUnavailableWhenUpsertingToPrimarySearch() {
        when(upsertCompanyService.upsertCompany(anyString(), any()))
                .thenReturn(new ResponseObject(SERVICE_UNAVAILABLE));
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

        ResponseEntity<?> responseEntity = companySearchController.upsertCompanyPrimarySearch(COMPANY_NUMBER, profileData);

        assertEquals(SERVICE_UNAVAILABLE, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns HTTP 200 OK given company number exists in index")
    void testDeleteWithCorrectCompanyNumberReturnsOkRequest() throws IOException {
        ResponseObject responseObject = new ResponseObject(DOCUMENT_DELETED);
        String companyNumber = "12345678";

        when(primarySearchDeleteService.deleteCompanyByNumber(companyNumber))
                .thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = companySearchController.deleteCompanyPrimarySearch(companyNumber);

        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns HTTP 400 BAD REQUEST given company number missing")
    void testDeleteWithMissingCompanyNumberReturnsBadRequest() throws IOException {
        String companyNumber = null;

        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = companySearchController.deleteCompanyPrimarySearch(companyNumber);

        assertEquals(DELETE_NOT_FOUND, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns HTTP 400 BAD REQUEST given company number empty")
    void testDeleteWithEmptyCompanyNumberReturnsBadRequest() throws IOException {
        String companyNumber = "";

        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = companySearchController.deleteCompanyPrimarySearch(companyNumber);

        assertEquals(DELETE_NOT_FOUND, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns HTTP 503 when API service is down")
    void testDeleteWhenApiIsDown() throws IOException {
        String companyNumber = "12345678";

        ResponseObject responseObject = new ResponseObject(DELETE_NOT_FOUND);

        when(primarySearchDeleteService.deleteCompanyByNumber(companyNumber)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

        ResponseEntity<?> responseEntity = companySearchController.deleteCompanyPrimarySearch(companyNumber);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
    }


    @Test
    @DisplayName("Test delete returns HTTP 503 when API service is down")
    void testDeleteWhenServiceIsDown() throws IOException {
        String companyNumber = "12345678";

        doThrow(new IOException()).when(primarySearchDeleteService).deleteCompanyByNumber(companyNumber);

        ResponseEntity<?> responseEntity = companySearchController.deleteCompanyPrimarySearch(companyNumber);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
    }

}