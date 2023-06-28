package uk.gov.companieshouse.search.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.delete.officers.DeleteOfficerService;

import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@RestController
@RequestMapping(value = "/officers-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class OfficerSearchController {
    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @Autowired
    private DeleteOfficerService deleteOfficerService;

    @DeleteMapping("/officers/{officer_id}")
    public ResponseEntity<Object> deleteOfficer(@PathVariable("officer_id") String officerId) {
        Map<String, Object> logMap = LoggingUtils.setUpOfficersDeleteLogging(officerId);
        getLogger().info("Attempting to delete an officer from the primary search index", logMap);

        ResponseObject responseObject;

        if (officerId == null || officerId.isEmpty()) {
            responseObject = new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
            getLogger().error("Path variable: officer_id missing from URL", logMap);
        } else {
            responseObject = deleteOfficerService.deleteOfficer(officerId);
        }

        return apiToResponseMapper.map(responseObject);
    }

}
