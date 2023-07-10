package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchType;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;
import uk.gov.companieshouse.search.api.service.upsert.disqualified.UpsertDisqualificationService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(value = "/disqualified-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class DisqualifiedSearchController {

    private static final String DISQUALIFICATION_SEARCH_TYPE = "disqualified-officer";
    private final ApiToResponseMapper apiToResponseMapper;
    private final UpsertDisqualificationService upsertDisqualificationService;
    private final PrimarySearchDeleteService primarySearchDeleteService;

    public DisqualifiedSearchController(ApiToResponseMapper apiToResponseMapper,
            UpsertDisqualificationService upsertDisqualificationService,
            PrimarySearchDeleteService primarySearchDeleteService) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.upsertDisqualificationService = upsertDisqualificationService;
        this.primarySearchDeleteService = primarySearchDeleteService;
    }

    @PutMapping("/disqualified-officers/{officer_id}")
    public ResponseEntity<Object> upsertOfficer(@PathVariable("officer_id") String officerId,
                                                       @Valid @RequestBody OfficerDisqualification officer) {

        Map<String, Object> logMap = LoggingUtils.setUpDisqualificationUpsertLogging(officer.getItems().get(0));
        getLogger().info("Attempting to upsert an officer to disqualification search index", logMap);

        ResponseObject responseObject;

        if (officerId == null || officerId.isEmpty()) {
            responseObject = new ResponseObject(ResponseStatus.UPSERT_ERROR);
        } else {
            responseObject = upsertDisqualificationService.upsertDisqualified(officer, officerId);
        }
        return apiToResponseMapper.map(responseObject);
    }

    @DeleteMapping("/delete/{officer_id}")
    public ResponseEntity<Object> deleteOfficer(@PathVariable("officer_id") String officerId) {
        Map<String, Object> logMap = LoggingUtils.setUpPrimarySearchDeleteLogging(officerId);
        getLogger().info("Attempting to delete an officer to disqualification search index", logMap);

        ResponseObject responseObject;

        if (officerId == null || officerId.isEmpty()) {
            responseObject = new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
        } else {
            responseObject = primarySearchDeleteService.deleteOfficer(new SearchType(officerId, DISQUALIFICATION_SEARCH_TYPE));
        }

        return apiToResponseMapper.map(responseObject);
    }
}
