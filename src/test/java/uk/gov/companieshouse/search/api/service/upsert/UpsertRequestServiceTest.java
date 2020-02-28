package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.exception.UpsertException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpsertRequestServiceTest {

    @Spy
    private UpsertRequestService upsertRequestService;

    private static final String ALPHA_SEARCH = "alpha_search";

    @Test
    @DisplayName("Test create index and update request is successful")
    void testCreateIndexRequestSuccessful() throws Exception {

        CompanyProfileApi company = createCompany();

        IndexRequest indexRequest = upsertRequestService.createIndexRequest(company);
        UpdateRequest updateRequest = upsertRequestService.createUpdateRequest(company, indexRequest);

        assertNotNull(indexRequest);
        assertNotNull(updateRequest);
        assertEquals(ALPHA_SEARCH, indexRequest.index());
        assertEquals(ALPHA_SEARCH, updateRequest.index());
    }

    @Test
    @DisplayName("Test create index request throws exception")
    void testCreateIndexThrowsException() throws Exception {

        CompanyProfileApi company = createCompany();

        when(upsertRequestService.createIndexRequest(company)).thenThrow(UpsertException.class);

        assertThrows(UpsertException.class,
            () -> upsertRequestService.createIndexRequest(company));
    }

    @Test
    @DisplayName("Test create update request throws exception")
    void testUpdateIndexThrowsException() throws Exception {

        CompanyProfileApi company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");

        when(upsertRequestService.createUpdateRequest(company, indexRequest))
            .thenThrow(UpsertException.class);

        assertThrows(UpsertException.class,
            () -> upsertRequestService.createUpdateRequest(company, indexRequest));
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
