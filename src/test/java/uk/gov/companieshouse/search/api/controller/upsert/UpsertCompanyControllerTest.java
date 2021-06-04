package uk.gov.companieshouse.search.api.controller.upsert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpsertCompanyControllerTest {

    @Mock
    private UpsertCompanyService mockUpsertCompanyService;

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @InjectMocks
    private UpsertCompanyController upsertCompanyController;

    @Test
    @DisplayName("Test upsert company is successful")
    void testUpsertSuccessful() {
        ResponseObject responseObject = new ResponseObject(DOCUMENT_UPSERTED);
        CompanyProfileApi company = createCompany();

        when(mockUpsertCompanyService.upsert(company)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = upsertCompanyController.upsertCompany(company);

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

        ResponseEntity<?> responseEntity = upsertCompanyController.upsertCompany(company);

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

        ResponseEntity<?> responseEntity = upsertCompanyController.upsertCompany(company);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
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
}
