package uk.gov.companieshouse.search.api.service.upsert.psc;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.psc.PscSummary;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import java.io.IOException;
import java.util.Map;

@Service
public class UpsertPscService {

    private final PrimarySearchRestClientService primarySearchRestClientService;
    private final PscUpsertRequestService pscUpsertRequestService;
    private final ConfiguredIndexNamesProvider indices;

    public UpsertPscService(PrimarySearchRestClientService primarySearchRestClientService,
                            PscUpsertRequestService pscUpsertRequestService,
                            ConfiguredIndexNamesProvider indices) {
        this.primarySearchRestClientService = primarySearchRestClientService;
        this.pscUpsertRequestService = pscUpsertRequestService;
        this.indices = indices;
    }

    public ResponseObject upsertPsc(PscSummary pscSummary, String pscId, String requestId) {
        Map<String, Object> logMap = LoggingUtils.setUpPrimaryOfficerSearchLogging(pscId, requestId, indices);
        LoggingUtils.getLogger().info(String.format("Attempting upsert for PSC: %s into primary search.", pscId), logMap);

        UpdateRequest updateRequest;
        try {
            updateRequest = pscUpsertRequestService.createUpdateRequest(pscSummary, pscId);
        } catch (UpsertException e) {
            LoggingUtils.getLogger().error(String.format("Error: could not process upsert for PSC: %s.", pscId), logMap);
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            primarySearchRestClientService.upsert(updateRequest);
        } catch (IOException e) {
            LoggingUtils.getLogger().error(String.format("Error: IOException when upserting PSC: %s.", pscId), logMap);
            return new ResponseObject(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        LoggingUtils.getLogger().info("Processed PSC search upsert.", logMap);
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }
}
