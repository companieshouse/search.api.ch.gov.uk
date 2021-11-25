package uk.gov.companieshouse.search.api.service.upsert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;
import uk.gov.companieshouse.search.api.service.upsert.advanced.AdvancedUpsertRequestService;
import uk.gov.companieshouse.search.api.service.upsert.alphabetical.AlphabeticalUpsertRequestService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpsertCompanyServiceTest {

    @Mock
    private AlphabeticalSearchRestClientService mockAlphabeticalRestClientService;

    @Mock
    private AlphabeticalUpsertRequestService mockAlphabeticalUpsertRequestService;

    @Mock
    private AdvancedSearchRestClientService mockAdvancedRestClientService;

    @Mock
    private AdvancedUpsertRequestService mockAdvancedUpsertRequestService;

    @Mock
    private AlphaKeyService mockAlphaKeyService;

    @InjectMocks
    private UpsertCompanyService upsertCompanyService;

    private static final String ORDERED_ALPHA_KEY_FIELD = "orderedAlphaKey";
    private static final String SAME_AS_ALPHA_KEY_FIELD = "sameAsAlphaKey";

    @Test
    @DisplayName("Test upsert is successful")
    void testUpsertIsSuccessful() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");

        when(mockAlphabeticalUpsertRequestService.createIndexRequest(company)).thenReturn(indexRequest);
        when(mockAlphabeticalUpsertRequestService.createUpdateRequest(
            company, indexRequest)).thenReturn(any(UpdateRequest.class));

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(DOCUMENT_UPSERTED, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test advanced search upsert is successful")
    void testAdvancedSearchUpsertIsSuccessful() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest("advanced_search");

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(createResponse());
        when(mockAdvancedUpsertRequestService.createIndexRequest(company, ORDERED_ALPHA_KEY_FIELD,
            SAME_AS_ALPHA_KEY_FIELD)).thenReturn(indexRequest);
        when(mockAdvancedUpsertRequestService.createUpdateRequest(
            company, ORDERED_ALPHA_KEY_FIELD, SAME_AS_ALPHA_KEY_FIELD, indexRequest)).thenReturn(any(UpdateRequest.class));

        ResponseObject responseObject = upsertCompanyService.upsertAdvanced(company);

        assertNotNull(responseObject);
        assertEquals(DOCUMENT_UPSERTED, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test advanced search upsert is successful null alpha key response")
    void testAdvancedSearchUpsertIsSuccessfulNullAlphaKeyResponse() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest("advanced_search");

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(null);
        when(mockAdvancedUpsertRequestService.createIndexRequest(company, "",
            "")).thenReturn(indexRequest);
        when(mockAdvancedUpsertRequestService.createUpdateRequest(
            company, "", "", indexRequest)).thenReturn(any(UpdateRequest.class));

        ResponseObject responseObject = upsertCompanyService.upsertAdvanced(company);

        assertNotNull(responseObject);
        assertEquals(DOCUMENT_UPSERTED, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test exception thrown during index request")
    void testExceptionThrownDuringIndexRequest() throws Exception {

        CompanyProfileApi company = createCompany();

        when(mockAlphabeticalUpsertRequestService.createIndexRequest(company)).thenThrow(UpsertException.class);

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(UPSERT_ERROR, responseObject.getStatus());
    }


    @Test
    @DisplayName("Test exception thrown during advanced search index request")
    void testExceptionThrownDuringAdvancedSearchIndexRequest() throws Exception {

        CompanyProfileApi company = createCompany();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(createResponse());
        when(mockAdvancedUpsertRequestService.createIndexRequest(company, ORDERED_ALPHA_KEY_FIELD,
            SAME_AS_ALPHA_KEY_FIELD)).thenThrow(UpsertException.class);

        ResponseObject responseObject = upsertCompanyService.upsertAdvanced(company);

        assertNotNull(responseObject);
        assertEquals(UPSERT_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test exception thrown during update request")
    void testExceptionThrownDuringUpdateRequest() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");

        when(mockAlphabeticalUpsertRequestService.createIndexRequest(company)).thenReturn(indexRequest);
        when(mockAlphabeticalUpsertRequestService.createUpdateRequest(
            company, indexRequest)).thenThrow(UpsertException.class);

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(UPSERT_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test exception thrown during advanced search update request")
    void testExceptionThrownDuringAdvancedSearchUpdateRequest() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest("advanced_search");

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(createResponse());
        when(mockAdvancedUpsertRequestService.createIndexRequest(company, ORDERED_ALPHA_KEY_FIELD,
            SAME_AS_ALPHA_KEY_FIELD)).thenReturn(indexRequest);
        when(mockAdvancedUpsertRequestService.createUpdateRequest(
            company, ORDERED_ALPHA_KEY_FIELD, SAME_AS_ALPHA_KEY_FIELD, indexRequest)).thenThrow(UpsertException.class);

        ResponseObject responseObject = upsertCompanyService.upsertAdvanced(company);

        assertNotNull(responseObject);
        assertEquals(UPSERT_ERROR, responseObject.getStatus());
    }


    @Test
    @DisplayName("Test exception thrown during upsert")
    void testExceptionThrownDuringUpsert() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");
        UpdateRequest updateRequest = new UpdateRequest("alpha_search", company.getCompanyNumber());

        when(mockAlphabeticalUpsertRequestService.createIndexRequest(company)).thenReturn(indexRequest);
        when(mockAlphabeticalUpsertRequestService.createUpdateRequest(
            company, indexRequest)).thenReturn(updateRequest);

        when(mockAlphabeticalRestClientService.upsert(updateRequest)).thenThrow(IOException.class);

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(UPDATE_REQUEST_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test exception thrown during advanced search upsert")
    void testExceptionThrownDuringAdvancedSearchUpsert() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest("advanced_search");
        UpdateRequest updateRequest = new UpdateRequest("advanced_search", company.getCompanyNumber());

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(createResponse());
        when(mockAdvancedUpsertRequestService.createIndexRequest(company, ORDERED_ALPHA_KEY_FIELD,
            SAME_AS_ALPHA_KEY_FIELD)).thenReturn(indexRequest);
        when(mockAdvancedUpsertRequestService.createUpdateRequest(
            company, ORDERED_ALPHA_KEY_FIELD, SAME_AS_ALPHA_KEY_FIELD, indexRequest)).thenReturn(updateRequest);

        when(mockAdvancedRestClientService.upsert(updateRequest)).thenThrow(IOException.class);

        ResponseObject responseObject = upsertCompanyService.upsertAdvanced(company);

        assertNotNull(responseObject);
        assertEquals(UPDATE_REQUEST_ERROR, responseObject.getStatus());
    }

    private AlphaKeyResponse createResponse() {
        AlphaKeyResponse alphaKeyResponse = new AlphaKeyResponse();
        alphaKeyResponse.setOrderedAlphaKey(ORDERED_ALPHA_KEY_FIELD);
        alphaKeyResponse.setSameAsAlphaKey(SAME_AS_ALPHA_KEY_FIELD);

        return alphaKeyResponse;
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
