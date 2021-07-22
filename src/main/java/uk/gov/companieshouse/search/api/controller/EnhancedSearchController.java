package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.LOCATION;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.enhanced.EnhancedSearchIndexService;

@RestController
@RequestMapping(value = "/enhanced-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnhancedSearchController {

    @Autowired
    private EnhancedSearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    private static final String COMPANY_NAME_QUERY_PARAM = "company_name";
    private static final String LOCATION_QUERY_PARAM = "location";
    private static final String INCORPORATED_FROM = "incorporated_from";

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<Object> search(@RequestParam(name = COMPANY_NAME_QUERY_PARAM, required = false) String companyName,
                                         @RequestParam(name = LOCATION_QUERY_PARAM, required = false) String location,
                                         @RequestParam(name = INCORPORATED_FROM, required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate incorporatedFrom,
                                         @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logIfNotNull(logMap, COMPANY_NAME, companyName);
        logIfNotNull(logMap, LOCATION, location);
        logMap.put(INDEX, LoggingUtils.ENHANCED_SEARCH_INDEX);
        getLogger().info("Search request received", logMap);
        logMap.remove(MESSAGE);

        EnhancedSearchQueryParams enhancedSearchQueryParams = mapEnhancedQueryParameters(companyName, location, incorporatedFrom);

        ResponseObject responseObject = searchIndexService.searchEnhanced(enhancedSearchQueryParams, requestId);

        return apiToResponseMapper.map(responseObject);
    }

    private EnhancedSearchQueryParams mapEnhancedQueryParameters(String companyName,
                                                                 String location,
                                                                 LocalDate incorporatedFrom) {

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(companyName);
        enhancedSearchQueryParams.setLocation(location);
        enhancedSearchQueryParams.setIncorporatedFrom(incorporatedFrom);

        return enhancedSearchQueryParams;
    }
}