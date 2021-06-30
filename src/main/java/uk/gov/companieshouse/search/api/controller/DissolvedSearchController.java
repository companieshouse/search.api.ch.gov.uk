package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_AFTER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_BEFORE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SIZE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.START_INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

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
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;


@RestController
@RequestMapping(value = "/dissolved-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class DissolvedSearchController {

    @Autowired
    private DissolvedSearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String MAX_SIZE_PARAM = "MAX_SIZE_PARAM";
    private static final String DISSOLVED_ALPHABETICAL_SEARCH_RESULT_MAX = "DISSOLVED_ALPHABETICAL_SEARCH_RESULT_MAX";
    private static final String DISSOLVED_SEARCH_RESULT_MAX = "DISSOLVED_SEARCH_RESULT_MAX";
    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String COMPANY_NAME_QUERY_PARAM = "q";
    private static final String SEARCH_TYPE_QUERY_PARAM = "search_type";
    private static final String START_INDEX_QUERY_PARAM = "start_index";
    private static final String ALPHABETICAL_SEARCH_TYPE = "alphabetical";
    private static final String BEST_MATCH_SEARCH_TYPE = "best-match";
    private static final String PREVIOUS_NAMES_SEARCH_TYPE = "previous-name-dissolved";
    private static final String SEARCH_BEFORE_PARAM = "search_before";
    private static final String SEARCH_AFTER_PARAM = "search_after";
    private static final String SIZE_PARAM = "size";

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<Object> searchCompanies(@RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
            @RequestParam(name = SEARCH_TYPE_QUERY_PARAM) String searchType,
            @RequestParam(name = SEARCH_BEFORE_PARAM, required = false) String searchBefore,
            @RequestParam(name = SEARCH_AFTER_PARAM, required = false) String searchAfter,
            @RequestParam(name = SIZE_PARAM, required = false) Integer size,
            @RequestParam(name = START_INDEX_QUERY_PARAM, required = false) Integer startIndex,
            @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(INDEX, LoggingUtils.INDEX_DISSOLVED);
        logIfNotNull(logMap, SEARCH_BEFORE, searchBefore);
        logIfNotNull(logMap, SEARCH_AFTER, searchAfter);
        logIfNotNull(logMap, SIZE, size);
        logIfNotNull(logMap, START_INDEX, startIndex);
        getLogger().info("Search request received", logMap);
        logMap.remove(MESSAGE);

        if (checkSearchTypeParam(searchType)) {

            int defaultSize = searchType.equals(ALPHABETICAL_SEARCH_TYPE) ?
                    environmentReader.getMandatoryInteger(DISSOLVED_ALPHABETICAL_SEARCH_RESULT_MAX) :
                    environmentReader.getMandatoryInteger(DISSOLVED_SEARCH_RESULT_MAX);

            try {
                size = SearchRequestUtils.checkResultsSize
                        (size, defaultSize, environmentReader.getMandatoryInteger(MAX_SIZE_PARAM));
            } catch (SizeException e) {
                getLogger().info(e.getMessage(), logMap);
                return apiToResponseMapper
                        .map(new ResponseObject(ResponseStatus.SIZE_PARAMETER_ERROR, null));
            }

            if (searchType.equals(ALPHABETICAL_SEARCH_TYPE)) {

                return getAlphabeticalSearch(companyName, searchBefore, searchAfter, size,
                        requestId);
            }

            if (searchType.equals(BEST_MATCH_SEARCH_TYPE) || searchType.equals(PREVIOUS_NAMES_SEARCH_TYPE)) {

                return getBestMatchOrPreviousNamesSearch(companyName, searchType, size, startIndex,
                        requestId);
            }
        }
        LoggingUtils.getLogger().error("The search_type parameter is incorrect, please try either "
                + "'alphabetical', 'best-match' or 'previous-name-dissolved': ", logMap);
        return apiToResponseMapper
                .map(new ResponseObject(ResponseStatus.REQUEST_PARAMETER_ERROR, null));
    }

    private ResponseEntity<Object> getBestMatchOrPreviousNamesSearch(
            @RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
            @RequestParam(name = SEARCH_TYPE_QUERY_PARAM) String searchType,
            Integer size,
            @RequestParam(name = START_INDEX_QUERY_PARAM, required = false) Integer startIndex,
            @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        if (startIndex == null || startIndex < 0) {
            startIndex = 0;
        }

        ResponseObject responseObject = searchIndexService.searchBestMatch(companyName, requestId,
                searchType, startIndex, size);

        return apiToResponseMapper.map(responseObject);
    }

    private ResponseEntity<Object> getAlphabeticalSearch(
            @RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
            @RequestParam(name = SEARCH_BEFORE_PARAM, required = false) String searchBefore,
            @RequestParam(name = SEARCH_AFTER_PARAM, required = false) String searchAfter,
            Integer size,
            @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        ResponseObject responseObject = searchIndexService.searchAlphabetical(companyName,
                searchBefore, searchAfter, size, requestId);

        return apiToResponseMapper.map(responseObject);
    }

    private boolean checkSearchTypeParam(String searchType) {

        return searchType.equals(ALPHABETICAL_SEARCH_TYPE) || searchType.equals(BEST_MATCH_SEARCH_TYPE)
                || searchType.equals(PREVIOUS_NAMES_SEARCH_TYPE);
    }
}
