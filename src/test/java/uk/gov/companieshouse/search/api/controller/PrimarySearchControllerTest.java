package uk.gov.companieshouse.search.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.mapper.AdvancedQueryParamMapper;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.delete.advanced.AdvancedSearchDeleteService;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchIndexService;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import javax.annotation.meta.When;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static uk.gov.companieshouse.search.api.constants.TestConstants.*;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.*;

@ExtendWith(MockitoExtension.class)
class PrimarySearchControllerTest {

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;

    @InjectMocks
    private CompanyPrimarySearchController primarySearchController;

    @Mock
    private PrimarySearchDeleteService primarySearchDeleteService;

    @Test
    @DisplayName("Test delete returns HTTP 200 OK given company number exists in index")
    void testDeleteWithCorrectCompanyNumberReturnsOkRequest() throws IOException {
        ResponseObject responseObject = new ResponseObject(DOCUMENT_DELETED);
        String companyNumber = "12345678";

        when(primarySearchDeleteService.deleteCompanyByNumber(companyNumber))
                .thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = primarySearchController.deleteCompanyPrimarySearch(companyNumber);

        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns HTTP 400 BAD REQUEST given company number missing")
    void testDeleteWithMissingCompanyNumberReturnsBadRequest() throws IOException {
        String companyNumber = null;

        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = primarySearchController.deleteCompanyPrimarySearch(companyNumber);

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

        ResponseEntity<?> responseEntity = primarySearchController.deleteCompanyPrimarySearch(companyNumber);

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
                .thenReturn(ResponseEntity.status(SERVICE_UNAVAILABLE).build());

        ResponseEntity<?> responseEntity = primarySearchController.deleteCompanyPrimarySearch(companyNumber);

        assertNotNull(responseEntity);
        assertEquals(SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
    }


    @Test
    @DisplayName("Test delete returns HTTP 503 when API service is down")
    void testDeleteWhenServiceIsDown() throws IOException {
        String companyNumber = "12345678";

        doThrow(new IOException()).when(primarySearchDeleteService).deleteCompanyByNumber(companyNumber);

        ResponseEntity<?> responseEntity = primarySearchController.deleteCompanyPrimarySearch(companyNumber);

        assertNotNull(responseEntity);
        assertEquals(SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
    }

}