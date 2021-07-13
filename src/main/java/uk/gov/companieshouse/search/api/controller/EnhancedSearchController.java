package uk.gov.companieshouse.search.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.enhanced.EnhancedSearchIndexService;

@RestController
@RequestMapping(value = "/enhanced-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnhancedSearchController {

    @Autowired
    private EnhancedSearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String COMPANY_NAME_QUERY_PARAM = "q";

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<Object> search() {
        ResponseObject responseObject = searchIndexService.searchEnhanced();

        return apiToResponseMapper.map(responseObject);
    }
}