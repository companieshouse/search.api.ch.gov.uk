package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.UpsertException;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class UpsertRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private static final String ID = "ID";
    private static final String COMPANY_TYPE = "company_type";
    private static final String ITEMS = "items";
    private static final String COMPANY_NUMBER = "company_number";
    private static final String COMPANY_STATUS = "company_status";
    private static final String CORPORATE_NAME = "corporate_name";
    private static final String RECORD_TYPE = "record_type";
    private static final String RECORD_TYPE_VALUE = "companies";
    private static final String LINKS = "links";
    private static final String SELF = "self";

    /**
     * Create an index request for document if it does not currently exist
     * @param company - Company sent over in REST call to be added/updated
     * @return {@link IndexRequest}
     * @throws UpsertException
     */
    public IndexRequest createIndexRequest(CompanyProfileApi company) throws UpsertException {

        IndexRequest indexRequest;
        try {
            LOG.info("Preparing index request for "  + company.getCompanyName());
            indexRequest = new IndexRequest("alpha_search")
                .source(buildRequest(company));
            return indexRequest;
        } catch (IOException e) {
            LOG.error("Failed to index a document for company number: " + company.getCompanyName());
            throw new UpsertException("Unable create index request");
        }
    }

    /**
     * If document already exists attempt to upsert the document
     * @param company - Company sent over in REST call to be added/updated
     * @param indexRequest
     * @return {@link UpdateRequest}
     * @throws UpsertException
     */
    public UpdateRequest createUpdateRequest(CompanyProfileApi company, IndexRequest indexRequest)
        throws UpsertException {

        UpdateRequest updateRequest;
        try {
            LOG.info("Attempt to upsert document if it does not exist for "  + company.getCompanyName());
            updateRequest = new UpdateRequest("alpha_search", company.getCompanyNumber())
                .doc(buildRequest(company))
                .upsert(indexRequest);

            return updateRequest;
        } catch (IOException e) {
            LOG.error("Failed to update a document for company: " + company.getCompanyName());
            throw new UpsertException("Unable to create update request");
        }
    }

    private XContentBuilder buildRequest(CompanyProfileApi company) throws IOException {
        return jsonBuilder()
            .startObject()
            .field(ID, company.getCompanyNumber())
            .field(COMPANY_TYPE, company.getType())
            .startObject(ITEMS)
            .field(COMPANY_NUMBER, company.getCompanyNumber())
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
