package uk.gov.companieshouse.search.api.controller;


import static uk.gov.companieshouse.search.api.logging.LoggingUtils.OFFICER_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.UPSERT_OFFICER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.upsert.disqualified.UpsertDisqualificationService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/disqualified-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class DisqualifiedSearchController {

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @Autowired
    private UpsertDisqualificationService upsertDisqualificationService;


    @PutMapping("/disqualified-officers/{officer_id}")
    public ResponseEntity<Object> upsertOfficer(@PathVariable("officer_id") String officerId,
                                                       @Valid @RequestBody OfficerDisqualification officer) {

        Map<String, Object> logMap = new HashMap<>();
        logMap.put(OFFICER_NAME, officer.getItems().get(0).getPersonName());
        logMap.put(UPSERT_OFFICER, officerId);
        getLogger().info("Attempting to upsert a natural officer to disqualification search index", logMap);

        ResponseObject responseObject;

        if (officerId == null || officerId.isEmpty()) {
            responseObject = new ResponseObject(ResponseStatus.UPSERT_ERROR);
        } else {
            responseObject = upsertDisqualificationService.upsertNaturalDisqualified(officer, officerId);
        }
        return apiToResponseMapper.map(responseObject);
    }
}
