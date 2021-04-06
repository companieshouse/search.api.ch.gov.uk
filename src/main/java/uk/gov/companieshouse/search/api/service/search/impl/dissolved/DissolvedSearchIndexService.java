package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISSOLVED_SEARCH_ALPHABETICAL;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISSOLVED_SEARCH_BEST_MATCH;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISSOLVED_SEARCH_PREVIOUS_NAMES_BEST_MATCH;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_TYPE;

@Service
public class DissolvedSearchIndexService {

    @Autowired
    private DissolvedSearchRequestService dissolvedSearchRequestService;

    private static final String SEARCHING_FOR_COMPANY_INFO = "searching for company";
    private static final String STANDARD_ERROR_MESSAGE = "An error occurred while trying to search for ";
    private static final String NO_RESULTS_FOUND = "No results were returned while searching for ";

    public DissolvedResponseObject searchAlphabetical(String companyName, String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(SEARCH_TYPE, DISSOLVED_SEARCH_ALPHABETICAL);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info(SEARCHING_FOR_COMPANY_INFO, logMap);

        DissolvedSearchResults searchResults;
        try {
            searchResults = dissolvedSearchRequestService.getSearchResults(companyName, requestId);
        } catch (SearchException e) {
            LoggingUtils.getLogger().error(STANDARD_ERROR_MESSAGE +
                            "alphabetical results on a dissolved company: ",
                    logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null) {
            LoggingUtils.getLogger().info("successful alphabetical search for dissolved company", logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LoggingUtils.getLogger().info(NO_RESULTS_FOUND +
                "alphabetical results on a dissolved company", logMap);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    public DissolvedResponseObject searchBestMatch(String companyName, String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(SEARCH_TYPE, DISSOLVED_SEARCH_BEST_MATCH);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info(SEARCHING_FOR_COMPANY_INFO, logMap);

        DissolvedSearchResults searchResults;
        try {
            searchResults = dissolvedSearchRequestService.getBestMatchSearchResults(companyName, requestId);
        } catch (SearchException e) {
            LoggingUtils.getLogger().error(STANDARD_ERROR_MESSAGE +
                            "best matches on a dissolved company: ",
                    logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null) {
            LoggingUtils.getLogger().info("successful best match search for dissolved company", logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LoggingUtils.getLogger().info(NO_RESULTS_FOUND +
                "best match on a dissolved company", logMap);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    public DissolvedResponseObject searchPreviousNamesBestMatch(String companyName, String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(SEARCH_TYPE, DISSOLVED_SEARCH_PREVIOUS_NAMES_BEST_MATCH);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info(SEARCHING_FOR_COMPANY_INFO, logMap);

        DissolvedSearchResults searchResults;
        try {
            searchResults = dissolvedSearchRequestService.getPreviousNamesBestMatchSearchResults(companyName, requestId);
        } catch (SearchException e) {
            LoggingUtils.getLogger().error(STANDARD_ERROR_MESSAGE +
                            "best matches for previous company name on a dissolved company: ",
                    logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null) {
            LoggingUtils.getLogger().info("successful best match search for previous company name on a dissolved company", logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LoggingUtils.getLogger().info(NO_RESULTS_FOUND +
                "best match search for previous company name on a dissolved company", logMap);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }
}
