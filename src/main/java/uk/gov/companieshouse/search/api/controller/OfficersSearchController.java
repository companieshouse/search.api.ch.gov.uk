package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchType;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;
import uk.gov.companieshouse.search.api.service.upsert.officers.UpsertOfficersService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@RestController
public class OfficersSearchController {

    private static final String OFFICER_SEARCH_TYPE = "officer";
    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private final ApiToResponseMapper apiToResponseMapper;
    private final UpsertOfficersService upsertOfficersService;
    private final PrimarySearchDeleteService primarySearchDeleteService;
    private final ConfiguredIndexNamesProvider indices;

    public OfficersSearchController(ApiToResponseMapper apiToResponseMapper,
            UpsertOfficersService upsertOfficersService, PrimarySearchDeleteService primarySearchDeleteService,
        ConfiguredIndexNamesProvider indices) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.upsertOfficersService = upsertOfficersService;
        this.primarySearchDeleteService = primarySearchDeleteService;
        this.indices = indices;
    }

    @PutMapping(value = "/officers-search/officers/{officer_id}")
    public ResponseEntity<Object> upsertOfficer(@PathVariable("officer_id") String officerId,
            @Valid @RequestBody AppointmentList appointmentList, @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        Map<String, Object> logMap = LoggingUtils.setUpPrimaryOfficerSearchLogging(officerId, requestId, indices);
        getLogger().info("Processing officers search upsert.", logMap);
        ResponseObject responseObject = upsertOfficersService.upsertOfficers(appointmentList, officerId, requestId);
        return apiToResponseMapper.map(responseObject);
    }

    @DeleteMapping("/officers-search/officers/{officer_id}")
    public ResponseEntity<Object> deleteOfficer(@PathVariable("officer_id") String officerId,
            @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        Map<String, Object> logMap = LoggingUtils.setUpPrimaryOfficerSearchLogging(officerId, requestId, indices);
        getLogger().info("Processing officers search delete.", logMap);

        ResponseObject responseObject;

        if (officerId == null || officerId.isEmpty()) {
            responseObject = new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
            getLogger().error("Error: officer_id missing from URL", logMap);
        } else {
            responseObject = primarySearchDeleteService.deleteOfficer(new SearchType(officerId,
                    OFFICER_SEARCH_TYPE), requestId);
        }

        return apiToResponseMapper.map(responseObject);
    }
}
