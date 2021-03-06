package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
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

    private static final String COMPANY_NAME_QUERY_PARAM = "q";
    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<Object> search(@RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
            @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(INDEX, LoggingUtils.ENHANCED_SEARCH_INDEX);
        getLogger().info("Search request received", logMap);
        logMap.remove(MESSAGE);

        ResponseObject responseObject = searchIndexService.searchEnhanced(companyName);

        return apiToResponseMapper.map(responseObject);
    }
}