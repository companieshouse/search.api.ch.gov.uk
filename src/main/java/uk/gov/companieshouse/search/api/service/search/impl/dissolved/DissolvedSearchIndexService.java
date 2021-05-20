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
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_TYPE;

@Service
public class DissolvedSearchIndexService {

    @Autowired
    private DissolvedSearchRequestService dissolvedSearchRequestService;

    private static final String SEARCHING_FOR_COMPANY_INFO = "searching for company";
    private static final String STANDARD_ERROR_MESSAGE = "An error occurred while trying to search for ";
    private static final String NO_RESULTS_FOUND = "No results were returned while searching for ";
    private static final String BEST_MATCH_SEARCH_TYPE = "best-match";

    public DissolvedResponseObject searchAlphabetical(String companyName, String requestId) {
        Map<String, Object> logMap = getLogMap(companyName, requestId, DISSOLVED_SEARCH_ALPHABETICAL);

        DissolvedSearchResults searchResults;
        try {
            searchResults = dissolvedSearchRequestService.getSearchResults(companyName, null, null, null, requestId);
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

    public DissolvedResponseObject searchBestMatch(String companyName, String requestId, String searchType) {
        Map<String, Object> logMap = getLogMap(companyName, requestId, searchType);

        DissolvedSearchResults searchResults;
        try {
            if (searchType.equals(BEST_MATCH_SEARCH_TYPE)) {
                searchResults = dissolvedSearchRequestService.getBestMatchSearchResults(companyName, requestId, searchType);
            } else {
                searchResults = dissolvedSearchRequestService.getPreviousNamesResults(companyName, requestId, searchType);
            }
        } catch (SearchException e) {
            LoggingUtils.getLogger().error(STANDARD_ERROR_MESSAGE +
                            "best matches on a " + searchType + " dissolved company: ",
                    logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null) {
            LoggingUtils.getLogger().info("successful best match search for " + searchType + " dissolved company", logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LoggingUtils.getLogger().info(NO_RESULTS_FOUND +
                "best match on a " + searchType + " dissolved company", logMap);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    private Map<String, Object> getLogMap(String companyName, String requestId, String searchType) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(SEARCH_TYPE, searchType);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info(SEARCHING_FOR_COMPANY_INFO, logMap);

        return logMap;
    }
}
