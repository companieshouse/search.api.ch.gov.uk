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
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;

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
        IndexRequest indexRequest = new IndexRequest();

        when(mockUpsertRequestService.createIndexRequest(company)).thenReturn(indexRequest);
        when(mockUpsertRequestService.createUpdateRequest(
            company, indexRequest)).thenReturn(any(UpdateRequest.class));

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        assertNotNull(responseObject);
        assertEquals(DOCUMENT_UPSERTED, responseObject.getStatus());
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
