package uk.gov.companieshouse.search.api.controller.upsert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpsertCompanyControllerTest {

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
        Company company = createCompany();

        when(mockUpsertCompanyService.upsert(company)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity responseEntity = upsertCompanyController.upsertCompany(company);

        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test upsert company failed to index or update document")
    void testUpsertFailedToIndexOrUpdate() {
        ResponseObject responseObject = new ResponseObject(UPSERT_ERROR);
        Company company = createCompany();

        when(mockUpsertCompanyService.upsert(company)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());

        ResponseEntity responseEntity = upsertCompanyController.upsertCompany(company);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test upsert failed during update request")
    void testUpsertFailedUpdateRequest() {
        ResponseObject responseObject = new ResponseObject(UPDATE_REQUEST_ERROR);
        Company company = createCompany();

        when(mockUpsertCompanyService.upsert(company)).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());

        ResponseEntity responseEntity = upsertCompanyController.upsertCompany(company);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private Company createCompany() {
        Company company = new Company();
        company.setId("ID");
        company.setCompanyType("company type");

        Items items = new Items();
        items.setCompanyNumber("company number");
        items.setCompanyStatus("company status");
        items.setCorporateName("corporate name");
        items.setRecordType("record type");
        company.setItems(items);

        Links links = new Links();
        links.setSelf("self");

        company.setLinks(links);

        return company;
    }
}
