package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.IndexException;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.service.upsert.UpsertRequestService;

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

        Company company = createCompany();

        IndexRequest indexRequest = upsertRequestService.createIndexRequest(company);
        UpdateRequest updateRequest = upsertRequestService.createUpdateRequest(company, indexRequest);

        assertNotNull(indexRequest);
        assertNotNull(updateRequest);
        assertEquals(ALPHA_SEARCH, indexRequest.index().toString());
        assertEquals(ALPHA_SEARCH, updateRequest.index().toString());
    }

    @Test
    @DisplayName("Test create index request throws exception")
    void testCreateIndexThrowsException() throws Exception {

        Company company = createCompany();

        when(upsertRequestService.createIndexRequest(company)).thenThrow(IndexException.class);

        assertThrows(IndexException.class,
            () -> upsertRequestService.createIndexRequest(company));
    }

    @Test
    @DisplayName("Test create update request throws exception")
    void testUpdateIndexThrowsException() throws Exception {

        Company company = createCompany();
        IndexRequest indexRequest = new IndexRequest("alpha_search");

        when(upsertRequestService.createUpdateRequest(company, indexRequest))
            .thenThrow(UpsertException.class);

        assertThrows(UpsertException.class,
            () -> upsertRequestService.createUpdateRequest(company, indexRequest));
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
