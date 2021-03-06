package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISSOLVED_SEARCH_ALPHABETICAL;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_AFTER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_BEFORE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_TYPE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SIZE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.START_INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

import java.util.Map;

@Service
public class DissolvedSearchIndexService {

    @Autowired
    private DissolvedSearchRequestService dissolvedSearchRequestService;

    private static final String SEARCHING_FOR_COMPANY_INFO = "searching for company";
    private static final String STANDARD_ERROR_MESSAGE = "An error occurred while trying to search for ";
    private static final String NO_RESULTS_FOUND = "No results were returned while searching for ";
    private static final String BEST_MATCH_SEARCH_TYPE = "best-match";

    public ResponseObject searchAlphabetical(String companyName, String searchBefore, String searchAfter,
            Integer size, String requestId) {
        Map<String, Object> logMap = getLogMap(companyName, requestId, DISSOLVED_SEARCH_ALPHABETICAL);
        logIfNotNull(logMap, SEARCH_BEFORE, searchBefore);
        logIfNotNull(logMap, SEARCH_AFTER, searchAfter);
        logIfNotNull(logMap, SIZE, size);
        logMap.remove(MESSAGE);
        

        SearchResults<Company> searchResults;
        try {
            getLogger().info("Searching using alphabetical search method", logMap);
            searchResults = dissolvedSearchRequestService.getSearchResults(companyName, searchBefore, searchAfter, size,
                    requestId);
        } catch (SearchException e) {
            getLogger().error(STANDARD_ERROR_MESSAGE + "alphabetical results on a dissolved company: ",
                    logMap);
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null && !searchResults.getItems().isEmpty()) {
            getLogger().info("successful alphabetical search for dissolved company", logMap);
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        getLogger().info(NO_RESULTS_FOUND + "alphabetical results on a dissolved company", logMap);
        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    public ResponseObject searchBestMatch(String companyName, String requestId, String searchType,
            Integer startIndex, Integer size) {
        Map<String, Object> logMap = getLogMap(companyName, requestId, searchType);
        logIfNotNull(logMap, START_INDEX, startIndex);
        logMap.remove(MESSAGE);

        SearchResults<?> searchResults;
        try {
            if (searchType.equals(BEST_MATCH_SEARCH_TYPE)) {
                getLogger().info("Searching using Best Match", logMap);
                searchResults = dissolvedSearchRequestService.getBestMatchSearchResults(companyName, requestId,
                        searchType, startIndex, size);
            } else {
                getLogger().info("Searching previous names", logMap);
                searchResults = dissolvedSearchRequestService.getPreviousNamesResults(companyName, requestId,
                        searchType, startIndex, size);
            }
        } catch (SearchException e) {
            getLogger()
                    .error(STANDARD_ERROR_MESSAGE + "best matches on a " + searchType + " dissolved company: ", logMap);
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if (searchResults.getItems() != null && !searchResults.getItems().isEmpty()) {
            getLogger().info("successful best match search for " + searchType + " dissolved company",
                    logMap);
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        getLogger().info(NO_RESULTS_FOUND + "best match on a " + searchType + " dissolved company",
                logMap);
        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    private Map<String, Object> getLogMap(String companyName, String requestId, String searchType) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(SEARCH_TYPE, searchType);
        logMap.put(INDEX, LoggingUtils.INDEX_DISSOLVED);
        getLogger().info(SEARCHING_FOR_COMPANY_INFO, logMap);

        return logMap;
    }
}
