package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchType;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;
import uk.gov.companieshouse.search.api.service.upsert.officers.UpsertOfficersService;

@RestController
public class OfficersSearchController {

    private static final String OFFICER_SEARCH_TYPE = "officer";
    private final ApiToResponseMapper apiToResponseMapper;
    private final UpsertOfficersService upsertOfficersService;
    private final PrimarySearchDeleteService primarySearchDeleteService;

    public OfficersSearchController(ApiToResponseMapper apiToResponseMapper,
            UpsertOfficersService upsertOfficersService, PrimarySearchDeleteService primarySearchDeleteService) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.upsertOfficersService = upsertOfficersService;
        this.primarySearchDeleteService = primarySearchDeleteService;
    }

    @PutMapping(value = "/officers-search/officers/{officer_id}")
    public ResponseEntity<Object> upsertOfficer(@PathVariable("officer_id") String officerId,
            @Valid @RequestBody AppointmentList appointmentList) {

        ResponseObject responseObject = upsertOfficersService.upsertOfficers(appointmentList, officerId);
        return apiToResponseMapper.map(responseObject);
    }

    @DeleteMapping("/officers-search/officers/{officer_id}")
    public ResponseEntity<Object> deleteOfficer(@PathVariable("officer_id") String officerId) {
        Map<String, Object> logMap = LoggingUtils.setUpPrimarySearchDeleteLogging(officerId);
        getLogger().info("Attempting to delete an officer from the primary search index", logMap);

        ResponseObject responseObject;

        if (officerId == null || officerId.isEmpty()) {
            responseObject = new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
            getLogger().error("Path variable: officer_id missing from URL", logMap);
        } else {
            responseObject = primarySearchDeleteService.deleteOfficer(new SearchType(officerId, OFFICER_SEARCH_TYPE));
        }

        return apiToResponseMapper.map(responseObject);
    }
}
