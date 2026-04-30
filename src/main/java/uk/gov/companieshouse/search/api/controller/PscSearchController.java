package uk.gov.companieshouse.search.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.psc.PscSummary;
import uk.gov.companieshouse.search.api.service.upsert.psc.UpsertPscService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;

@RestController
@RequestMapping(value = "/psc-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class PscSearchController {

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private final ApiToResponseMapper apiToResponseMapper;
    private final UpsertPscService upsertPscService;
    // private final ConfiguredIndexNamesProvider indices;

    public PscSearchController(ApiToResponseMapper apiToResponseMapper,
                               UpsertPscService upsertPscService,
                               ConfiguredIndexNamesProvider indices) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.upsertPscService = upsertPscService;
        this.indices = indices;
    }

    @PutMapping(value = "/psc-search/psc/{psc_id}")
    public ResponseEntity<Object> upsertPsc(@PathVariable("psc_id") String pscId,
                                            @Valid @RequestBody PscSummary pscSummary,
                                            @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        ResponseObject responseObject = upsertPscService.upsertPsc(pscSummary, pscId, requestId);
        return apiToResponseMapper.map(responseObject);
    }
}
