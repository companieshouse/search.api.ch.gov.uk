package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.IndexException;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class UpsertCompanyService {

    @Autowired
    private RestHighLevelClient client;

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private static final String ID = "ID";
    private static final String COMPANY_TYPE = "company_type";
    private static final String ITEMS = "items";
    private static final String COMPANY_NUMBER = "company_number";
    private static final String COMPANY_STATUS = "company_status";
    private static final String CORPORATE_NAME = "corporate_name";
    private static final String RECORD_TYPE = "record_type";
    private static final String LINKS = "links";
    private static final String SELF = "self";

    /**
     * Upserts a new document to elastic search. If a document does not exist it is added.
     * If the document does exist it is updated.
     *
     * @param company - Company sent over in REST call to be added/updated
     * @return {@link ResponseObject}
     */
    public ResponseObject upsert(Company company) {

        IndexRequest indexRequest;
        UpdateRequest updateRequest;

        try {
            indexRequest = createIndexRequest(company);
            updateRequest = createUpdateRequest(company, indexRequest);
        } catch (IndexException | UpsertException e) {
            LOG.error("An error occured attempting upsert the document");
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOG.error("An error occured attempting to make an update request: " + updateRequest);
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }

    /**
     * Create an index request for document if it does not currently exist
     * @param company - Company sent over in REST call to be added/updated
     * @return {@link IndexRequest}
     * @throws IndexException
     */
    private IndexRequest createIndexRequest(Company company) throws IndexException {

        IndexRequest indexRequest;
        try {
            LOG.info("Preparing index request for "  + company.getId());
            indexRequest = new IndexRequest("alpha_search")
                .source(jsonBuilder()
                .startObject()
                    .field(ID, company.getId())
                    .field(COMPANY_TYPE, company.getCompanyType())
                    .startObject(ITEMS)
                        .field(COMPANY_NUMBER, company.getItems().getCompanyNumber())
                        .field(COMPANY_STATUS, company.getItems().getCompanyStatus())
                        .field(CORPORATE_NAME, company.getItems().getCorporateName())
                        .field(RECORD_TYPE, company.getItems().getRecordType())
                    .endObject()
                    .startObject(LINKS)
                        .field(SELF, company.getLinks().getSelf())
                    .endObject()
                .endObject());
            return indexRequest;
        } catch (IOException e) {
            LOG.error("Failed to index a document");
            throw new IndexException("Unable to index a document");
        }
    }

    /**
     * If document already exists attempt to upsert the document
     * @param company - Company sent over in REST call to be added/updated
     * @param indexRequest
     * @return {@link UpdateRequest}
     * @throws UpsertException
     */
    private UpdateRequest createUpdateRequest(Company company, IndexRequest indexRequest)
        throws UpsertException {

        UpdateRequest updateRequest;
        try {
            LOG.info("Attempt to upsert document if it does not exist for "  + company.getId());
            updateRequest = new UpdateRequest("alpha_search", company.getId())
                .doc(jsonBuilder()
                .startObject()
                    .field(ID, company.getId())
                    .field(COMPANY_TYPE, company.getCompanyType())
                    .startObject(ITEMS)
                        .field(COMPANY_NUMBER, company.getItems().getCompanyNumber())
                        .field(COMPANY_STATUS, company.getItems().getCompanyStatus())
                        .field(CORPORATE_NAME, company.getItems().getCorporateName())
                        .field(RECORD_TYPE, company.getItems().getRecordType())
                    .endObject()
                    .startObject(LINKS)
                        .field(SELF, company.getLinks().getSelf())
                    .endObject()
                .endObject())
                .upsert(indexRequest);

        return updateRequest;
        } catch (IOException e) {
          LOG.error("Failed to upsert document");
          throw new UpsertException("Unable to upsert document");
        }
    }
}
