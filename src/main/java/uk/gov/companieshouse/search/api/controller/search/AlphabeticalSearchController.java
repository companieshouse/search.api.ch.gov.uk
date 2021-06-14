package uk.gov.companieshouse.search.api.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;

import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX_ALPHABETICAL;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_AFTER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_BEFORE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SIZE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.createLoggingMap;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

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

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String DEFAULT_SEARCH_RESULTS = "DEFAULT_SEARCH_RESULTS";
    private static final String MAX_SEARCH_RESULTS = "MAX_SEARCH_RESULTS";

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<?> searchByCorporateName(@RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
                                                   @RequestParam(name = SEARCH_BEFORE_PARAM, required = false) String searchBefore,
                                                   @RequestParam(name = SEARCH_AFTER_PARAM, required = false) String searchAfter,
                                                   @RequestParam(name = SIZE_PARAM, required = false) Integer size,
                                                   @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        Map<String, Object> logMap = createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(INDEX, INDEX_ALPHABETICAL);
        logIfNotNull(logMap, SEARCH_BEFORE, searchBefore);
        logIfNotNull(logMap, SEARCH_AFTER, searchAfter);
        logIfNotNull(logMap, SIZE, size);
        getLogger().info("Search request received", logMap);

        try {
            size = SearchRequestUtils.checkResultsSize
                (size, environmentReader.getMandatoryInteger(DEFAULT_SEARCH_RESULTS),
                    environmentReader.getMandatoryInteger(MAX_SEARCH_RESULTS));
        } catch (SizeException e) {
            getLogger().info(e.getMessage(), logMap);
            return apiToResponseMapper
                .map(new ResponseObject(ResponseStatus.SIZE_PARAMETER_ERROR, null));
        }

        ResponseObject responseObject = searchIndexService
            .search(companyName, searchBefore, searchAfter, size, requestId);

        return apiToResponseMapper.map(responseObject);
    }
}
