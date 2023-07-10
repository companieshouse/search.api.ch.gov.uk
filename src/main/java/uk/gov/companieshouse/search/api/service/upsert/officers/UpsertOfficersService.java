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

@Service
public class UpsertOfficersService {

    private final PrimarySearchRestClientService primarySearchRestClientService;

    private final OfficersUpsertRequestService officersUpsertRequestService;

    public UpsertOfficersService(PrimarySearchRestClientService primarySearchRestClientService,
            OfficersUpsertRequestService officersUpsertRequestService) {
        this.primarySearchRestClientService = primarySearchRestClientService;
        this.officersUpsertRequestService = officersUpsertRequestService;
    }

    public ResponseObject upsertOfficers(AppointmentList appointmentList, String officerId) {
        Map<String, Object> logMap = LoggingUtils.setUpOfficersAppointmentsUpsertLogging(officerId);
        getLogger().info("Upserting officer's appointments to primary index", logMap);

        UpdateRequest updateRequest;
        try {
            updateRequest = officersUpsertRequestService.createUpdateRequest(appointmentList, officerId);
        } catch (UpsertException e) {
            getLogger().error("An error occurred attempting upsert the document to primary search "
                    + "index", logMap);
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            primarySearchRestClientService.upsert(updateRequest);
        } catch (IOException e) {
            getLogger().error("IOException when upserting an officer to primary search "
                    + "index", logMap);
            return new ResponseObject(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        getLogger().info("Upsert successful to officers search index", logMap);
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }
}
