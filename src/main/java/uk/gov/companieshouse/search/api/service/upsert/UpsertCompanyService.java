package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class UpsertCompanyService {

    @Autowired
    private AlphabeticalSearchRestClientService restClientService;

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
    public ResponseObject upsert(CompanyProfileApi company) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LoggingUtils.COMPANY_NAME, company.getCompanyName());
        logMap.put(LoggingUtils.COMPANY_NUMBER, company.getCompanyNumber());
        LoggingUtils.getLogger().info("Upserting company underway", logMap);

        IndexRequest indexRequest;
        UpdateRequest updateRequest;

        try {
            indexRequest = upsertRequestService.createIndexRequest(company);
            updateRequest = upsertRequestService.createUpdateRequest(company, indexRequest);
        } catch (UpsertException e) {
            LoggingUtils.getLogger().error("An error occured attempting upsert the document", logMap);
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            restClientService.upsert(updateRequest);
        } catch (IOException e) {
            LoggingUtils.getLogger().error("An error occured attempting to make an update request: " + updateRequest);
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        LoggingUtils.getLogger().info("Upsert successful for ", logMap);
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }
}
