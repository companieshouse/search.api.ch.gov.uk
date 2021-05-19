package uk.gov.companieshouse.search.api.controller.search;

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
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;

@RestController
@RequestMapping(value = "/alphabetical-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlphabeticalSearchController {

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String COMPANY_NAME_QUERY_PARAM = "q";
    private static final String SEARCH_BEFORE_PARAM = "search_before";
    private static final String SEARCH_AFTER_PARAM = "search_after";
    private static final String SIZE_PARAM = "size";

    @Autowired
    private AlphabeticalSearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity searchByCorporateName(@RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
                                                @RequestParam(name = SEARCH_BEFORE_PARAM, required=false) String searchBefore,
                                                @RequestParam(name = SEARCH_AFTER_PARAM, required=false) String searchAfter,
                                                @RequestParam(name = SIZE_PARAM, required=false) Integer size,
                                                @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, companyName);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_ALPHABETICAL);
        LoggingUtils.getLogger().info("Search request received", logMap);
        
        ResponseObject responseObject = searchIndexService
            .search(companyName, searchBefore, searchAfter, size, requestId);

        return apiToResponseMapper.map(responseObject);
    }
}
