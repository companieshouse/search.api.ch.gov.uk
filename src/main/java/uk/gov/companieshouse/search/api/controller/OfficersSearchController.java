package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.Map;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.upsert.officers.UpsertOfficersService;

@RestController
public class OfficersSearchController {

    private final ApiToResponseMapper apiToResponseMapper;

    private final UpsertOfficersService upsertOfficersService;

    public OfficersSearchController(ApiToResponseMapper apiToResponseMapper,
            UpsertOfficersService upsertOfficersService) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.upsertOfficersService = upsertOfficersService;
    }

    @PutMapping(value = "/officers-search/officers/{officer_id}")
    public ResponseEntity<Object> upsertOfficer(@PathVariable("officer_id") String officerId,
            @Valid @RequestBody AppointmentList appointmentList) {

        Map<String, Object> logMap =
                LoggingUtils.setUpOfficersAppointmentsUpsertLogging(appointmentList.getItems().get(0));
        getLogger().debug("Attempting to upsert an officer's appointments to primary search index",
                logMap);

        ResponseObject responseObject;

        if (StringUtils.isBlank(officerId)) {
            responseObject = new ResponseObject(ResponseStatus.UPSERT_ERROR);
        } else {
            responseObject = upsertOfficersService.upsertOfficers(appointmentList, officerId);
        }
        return apiToResponseMapper.map(responseObject);
    }
}
