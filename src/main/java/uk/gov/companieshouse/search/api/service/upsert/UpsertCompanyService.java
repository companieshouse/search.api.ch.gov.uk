package uk.gov.companieshouse.search.api.service.upsert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;

@Service
public class UpsertCompanyService {

    @Autowired
    private AlphabeticalSearchRestClientService restClientService;

    @Autowired
    private UpsertRequestService upsertRequestService;

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
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_ALPHABETICAL);
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
            LoggingUtils.getLogger().error("IOException when upserting company", logMap);
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        LoggingUtils.getLogger().info("Upsert successful for ", logMap);
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }
}
