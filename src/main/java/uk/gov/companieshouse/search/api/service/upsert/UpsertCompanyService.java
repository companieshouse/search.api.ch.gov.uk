package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;

import java.io.IOException;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class UpsertCompanyService {

    @Autowired
    private RestClientService restClientService;

    @Autowired
    private UpsertRequestService upsertRequestService;

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

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
            indexRequest = upsertRequestService.createIndexRequest(company);
            updateRequest = upsertRequestService.createUpdateRequest(company, indexRequest);
        } catch (UpsertException e) {
            LOG.error("An error occured attempting upsert the document");
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            restClientService.upsert(updateRequest);
        } catch (IOException e) {
            LOG.error("An error occured attempting to make an update request: " + updateRequest);
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        LOG.info("Upsert successful for " + company.getId());
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }
}
