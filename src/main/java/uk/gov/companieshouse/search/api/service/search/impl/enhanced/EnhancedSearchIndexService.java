package uk.gov.companieshouse.search.api.service.search.impl.enhanced;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.LOCATION;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.NO_RESULTS_FOUND;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.STANDARD_ERROR_MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SUCCESSFUL_SEARCH;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

@Service
public class EnhancedSearchIndexService {

    @Autowired
    private EnhancedSearchRequestService enhancedSearchRequestService;

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    public ResponseObject searchEnhanced(EnhancedSearchQueryParams queryParams, String requestId) {

        Map<String, Object> logMap = getLogMap(queryParams, requestId);
        logMap.remove(MESSAGE);

        SearchResults<Company> searchResults;
        try {
            searchResults = enhancedSearchRequestService.getSearchResults(queryParams, requestId);
        } catch (SearchException se) {
            getLogger()
                    .error(STANDARD_ERROR_MESSAGE, logMap);
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null && !searchResults.getItems().isEmpty()) {
            getLogger().info(SUCCESSFUL_SEARCH, logMap);
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        getLogger().info(NO_RESULTS_FOUND, logMap);
        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    private Map<String, Object> getLogMap(EnhancedSearchQueryParams queryParams, String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, queryParams.getCompanyName());
        logIfNotNull(logMap, LOCATION, queryParams.getLocation());
        logMap.put(INDEX, LoggingUtils.ENHANCED_SEARCH_INDEX);
        getLogger().info("enhanced search filters", logMap);

        return logMap;
    }
}