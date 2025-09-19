package uk.gov.companieshouse.search.api.service.upsert.officers;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.io.IOException;
import java.util.Map;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class UpsertOfficersService {

    private final PrimarySearchRestClientService primarySearchRestClientService;

    private final OfficersUpsertRequestService officersUpsertRequestService;

    private final ConfiguredIndexNamesProvider indices;

    public UpsertOfficersService(PrimarySearchRestClientService primarySearchRestClientService,
            OfficersUpsertRequestService officersUpsertRequestService,
        ConfiguredIndexNamesProvider indices) {
        this.primarySearchRestClientService = primarySearchRestClientService;
        this.officersUpsertRequestService = officersUpsertRequestService;
        this.indices = indices;
    }

    public ResponseObject<String> upsertOfficers(AppointmentList appointmentList, String officerId, String requestId) {
        Map<String, Object> logMap =
            LoggingUtils.setUpPrimaryOfficerSearchLogging(officerId, requestId, indices);
        getLogger().info(String.format("Attempting upsert for officer: %s into primary search.", officerId), logMap);

        UpdateRequest updateRequest;
        try {
            updateRequest = officersUpsertRequestService.createUpdateRequest(appointmentList, officerId);
        } catch (UpsertException e) {
            getLogger().error(String.format("Error: could not process upsert for officers: %s.", officerId), logMap);
            return new ResponseObject<>(ResponseStatus.UPSERT_ERROR);
        }

        try {
            primarySearchRestClientService.upsert(updateRequest);
        } catch (IOException e) {
            getLogger().error(String.format("Error: IOException when upserting officer: %s.", officerId), logMap);
            return new ResponseObject<>(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject<>(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        getLogger().info("Processed officers search upsert.", logMap);
        return new ResponseObject<>(ResponseStatus.DOCUMENT_UPSERTED);
    }
}
