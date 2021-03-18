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
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;

@RestController
@RequestMapping(value = "/dissolved-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class DissolvedSearchController {

    @Autowired
    private DissolvedSearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String COMPANY_NAME_QUERY_PARAM = "q";
    private static final String SEARCH_TYPE_QUERY_PARAM = "search_type";
    private static final String ALPHABETICAL_SEARCH_TYPE = "alphabetical";
    private static final String BEST_MATCH_SEARCH_TYPE = "best-match";


    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<Object> searchCompanies(@RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
                                                  @RequestParam(name = SEARCH_TYPE_QUERY_PARAM) String searchType,
                                                  @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, companyName);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info("Search request received", logMap);

        if (checkSearchTypeParam(searchType)) {

            if (searchType.equals(ALPHABETICAL_SEARCH_TYPE)) {
                DissolvedResponseObject responseObject = searchIndexService
                        .searchAlphabetical(companyName, requestId);

                return apiToResponseMapper.mapDissolved(responseObject);
            }

            if (searchType.equals(BEST_MATCH_SEARCH_TYPE)) {
                DissolvedResponseObject responseObject = searchIndexService
                        .searchBestMatch(companyName, requestId);

                return apiToResponseMapper.mapDissolved(responseObject);
            }
        }
        LoggingUtils.getLogger().error("The search_type parameter is incorrect, please try either " +
                        "'alphabetical' or 'best-match': ", logMap);
        return apiToResponseMapper.mapDissolved(new DissolvedResponseObject(ResponseStatus.SEARCH_ERROR, null));
    }

    private boolean checkSearchTypeParam(String searchType) {

        return searchType.equals(ALPHABETICAL_SEARCH_TYPE)
                || searchType.equals(BEST_MATCH_SEARCH_TYPE);
    }
}
