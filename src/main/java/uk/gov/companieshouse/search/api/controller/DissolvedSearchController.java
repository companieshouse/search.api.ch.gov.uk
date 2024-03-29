package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;


@RestController
@RequestMapping(value = "/dissolved-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class DissolvedSearchController {

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


    private final DissolvedSearchIndexService searchIndexService;
    private final ApiToResponseMapper apiToResponseMapper;
    private final EnvironmentReader environmentReader;
    private final ConfiguredIndexNamesProvider indices;


    public DissolvedSearchController(DissolvedSearchIndexService searchIndexService,
        ApiToResponseMapper apiToResponseMapper, EnvironmentReader environmentReader,
        ConfiguredIndexNamesProvider indices) {
        this.searchIndexService = searchIndexService;
        this.apiToResponseMapper = apiToResponseMapper;
        this.environmentReader = environmentReader;
        this.indices = indices;
    }

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<Object> searchCompanies(@RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
            @RequestParam(name = SEARCH_TYPE_QUERY_PARAM) String searchType,
            @RequestParam(name = SEARCH_BEFORE_PARAM, required = false) String searchBefore,
            @RequestParam(name = SEARCH_AFTER_PARAM, required = false) String searchAfter,
            @RequestParam(name = SIZE_PARAM, required = false) Integer size,
            @RequestParam(name = START_INDEX_QUERY_PARAM, required = false) Integer startIndex,
            @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .companyName(companyName)
                .indexName(indices.dissolved())
                .searchBefore(searchBefore)
                .searchAfter(searchAfter)
                .size(String.valueOf(size))
                .startIndex(String.valueOf(startIndex))
                .build().getLogMap();
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
