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
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_TYPE;

@Service
public class DissolvedSearchIndexService {

    @Autowired
    private DissolvedSearchRequestService dissolvedSearchRequestService;

    public DissolvedResponseObject searchAlphabetical(String companyName, String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(SEARCH_TYPE, DISSOLVED_SEARCH_ALPHABETICAL);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info("searching for company", logMap);

        DissolvedSearchResults searchResults;
        try {
            searchResults = dissolvedSearchRequestService.getSearchResults(companyName, requestId);
        } catch (SearchException e) {
            LoggingUtils.getLogger().error("An error occurred while trying to search for " +
                            "alphabetical results on a dissolved company: ",
                    logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null) {
            LoggingUtils.getLogger().info("successful alphabetical search for dissolved company", logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LoggingUtils.getLogger().info("No results were returned while searching for " +
                "alphabetical results on a dissolved company", logMap);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    public DissolvedResponseObject searchBestMatch(String companyName, String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(SEARCH_TYPE, DISSOLVED_SEARCH_BEST_MATCH);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info("searching for company", logMap);

        DissolvedSearchResults searchResults;
        try {
            searchResults = dissolvedSearchRequestService.getBestMatchSearchResults(companyName, requestId);
        } catch (SearchException e) {
            LoggingUtils.getLogger().error("An error occurred while trying to search for " +
                            "best matches on a dissolved company: ",
                    logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null) {
            LoggingUtils.getLogger().info("successful best match search for dissolved company", logMap);
            return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LoggingUtils.getLogger().info("No results were returned while searching for " +
                "best match on a dissolved company", logMap);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }
}
