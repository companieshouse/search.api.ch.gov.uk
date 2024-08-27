package uk.gov.companieshouse.search.api.service.upsert.alphabetical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.json.JsonXContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class AlphabeticalUpsertRequestServiceTest {

    @InjectMocks
    private AlphabeticalUpsertRequestService alphabeticalUpsertRequestService;

    @Mock
    private AlphaKeyService mockAlphaKeyService;

    @Mock
    private AlphabeticalSearchUpsertRequest mockAlphabeticalSearchUpsertRequest;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    private static final String ALPHA_SEARCH = "alpha_search";

    private static final String ID = "ID";
    private static final String COMPANY_TYPE = "company_type";
    private static final String ITEMS = "items";
    private static final String COMPANY_NUMBER = "12345";
    private static final String COMPANY_STATUS = "company_status";
    private static final String CORPORATE_NAME = "corporate_name";
    private static final String RECORD_TYPE = "record_type";
    private static final String RECORD_TYPE_VALUE = "companies";
    private static final String LINKS = "links";
    private static final String SELF = "self";
    private static final String ORDERED_ALPHA_KEY = "ordered_alpha_key";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";

    private static final String ORDERED_ALPHA_KEY_FIELD = "orderedAlphaKey";
    private static final String ORDERED_ALPHA_KEY_WITH_ID_FIELD = "orderedAlphaKey:12345";

    @BeforeEach
    void init() {
        when(mockAlphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(createResponse());
        when(indices.alphabetical()).thenReturn(ALPHA_SEARCH);
    }

    @Test
    @DisplayName("Test create index and update request is successful")
    void testCreateIndexRequestSuccessful() throws Exception {

        CompanyProfileApi company = createCompany();

        when(mockAlphabeticalSearchUpsertRequest.buildRequest(company, ORDERED_ALPHA_KEY_FIELD,
            ORDERED_ALPHA_KEY_WITH_ID_FIELD))
            .thenReturn(createRequest(company, ORDERED_ALPHA_KEY_FIELD,
                ORDERED_ALPHA_KEY_WITH_ID_FIELD));

        IndexRequest indexRequest = alphabeticalUpsertRequestService.createIndexRequest(company);
        UpdateRequest updateRequest = alphabeticalUpsertRequestService.createUpdateRequest(company, indexRequest);

        assertNotNull(indexRequest);
        assertNotNull(updateRequest);
        assertEquals(ALPHA_SEARCH, indexRequest.index());
        assertEquals(ALPHA_SEARCH, updateRequest.index());
    }

    @Test
    @DisplayName("Test create index request throws exception")
    void testCreateIndexThrowsException() throws Exception {

        CompanyProfileApi company = createCompany();

        when(mockAlphabeticalSearchUpsertRequest.buildRequest(company, ORDERED_ALPHA_KEY_FIELD,
            ORDERED_ALPHA_KEY_WITH_ID_FIELD)).thenThrow(IOException.class);

        assertThrows(UpsertException.class,
            () -> alphabeticalUpsertRequestService.createIndexRequest(company));
    }

    @Test
    @DisplayName("Test create update request throws exception")
    void testUpdateIndexThrowsException() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest(ALPHA_SEARCH);

        when(mockAlphabeticalSearchUpsertRequest.buildRequest(company, ORDERED_ALPHA_KEY_FIELD,
            ORDERED_ALPHA_KEY_WITH_ID_FIELD)).thenThrow(IOException.class);

        assertThrows(UpsertException.class,
            () -> alphabeticalUpsertRequestService.createUpdateRequest(company, indexRequest));
    }

    private CompanyProfileApi createCompany() {
        CompanyProfileApi company = new CompanyProfileApi();
        company.setType(COMPANY_TYPE);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setCompanyName(CORPORATE_NAME);

        Map<String, String> links = new HashMap<>();
        links.put("self", "company/00000000");
        company.setLinks(links);

        return company;
    }

    private AlphaKeyResponse createResponse() {
        AlphaKeyResponse alphaKeyResponse = new AlphaKeyResponse();
        alphaKeyResponse.setOrderedAlphaKey(ORDERED_ALPHA_KEY_FIELD);

        return alphaKeyResponse;
    }

    private XContentBuilder createRequest(CompanyProfileApi company, String orderedAlphaKey,
                                          String orderedAlphaKeyWithID) throws Exception{
        return JsonXContent.contentBuilder()
            .startObject()
            .field(ID, company.getCompanyNumber())
            .field(ORDERED_ALPHA_KEY_WITH_ID, orderedAlphaKeyWithID)
            .field(COMPANY_TYPE, company.getType())
            .startObject(ITEMS)
            .field(COMPANY_NUMBER, company.getCompanyNumber())
            .field(ORDERED_ALPHA_KEY, orderedAlphaKey)
            .field(COMPANY_STATUS, company.getCompanyStatus())
            .field(CORPORATE_NAME, company.getCompanyName())
            .field(RECORD_TYPE, RECORD_TYPE_VALUE)
            .endObject()
            .startObject(LINKS)
            .field(SELF, company.getLinks().get(SELF))
            .endObject()
            .endObject();
    }
}
