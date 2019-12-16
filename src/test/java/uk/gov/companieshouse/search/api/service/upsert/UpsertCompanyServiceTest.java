package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpsertCompanyServiceTest {

    @Mock
    private RestClientService mockRestClientService;

    @Mock
    private UpsertRequestService mockUpsertRequestService;

    @InjectMocks
    private UpsertCompanyService upsertCompanyService;

    @Test
    @DisplayName("Test upsert is successful")
    void testUpsertIsSuccessful() throws Exception {

        Company company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");

        when(mockUpsertRequestService.createIndexRequest(company)).thenReturn(indexRequest);
        when(mockUpsertRequestService.createUpdateRequest(
            company, indexRequest)).thenReturn(any(UpdateRequest.class));

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(DOCUMENT_UPSERTED, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test exception thrown during index request")
    void testExceptionThrownDuringIndexRequest() throws Exception {

        Company company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");

        when(mockUpsertRequestService.createIndexRequest(company)).thenThrow(UpsertException.class);

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(UPSERT_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test exception thrown during update request")
    void testExceptionThrownDuringUpdateRequest() throws Exception {

        Company company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");

        when(mockUpsertRequestService.createIndexRequest(company)).thenReturn(indexRequest);
        when(mockUpsertRequestService.createUpdateRequest(
            company, indexRequest)).thenThrow(UpsertException.class);

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(UPSERT_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test exception thrown during upsert")
    void testExceptionThrownDuringUpsert() throws Exception {

        Company company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");
        UpdateRequest updateRequest = new UpdateRequest("alpha_search", company.getId());

        when(mockUpsertRequestService.createIndexRequest(company)).thenReturn(indexRequest);
        when(mockUpsertRequestService.createUpdateRequest(
            company, indexRequest)).thenReturn(updateRequest);

        when(mockRestClientService.upsert(updateRequest)).thenThrow(IOException.class);

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(UPDATE_REQUEST_ERROR, responseObject.getStatus());
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
