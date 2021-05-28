package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

@Service
public class AlphabeticalSearchIndexService implements SearchIndexService {

    @Autowired
    private SearchRequestService<Company> searchRequestService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseObject search(String corporateName, String searchBefore, String searchAfter, Integer size,
            String requestId) {


        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, corporateName);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_ALPHABETICAL);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SEARCH_BEFORE, searchBefore);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SEARCH_AFTER, searchAfter);
        LoggingUtils.logIfNotNull(logMap, LoggingUtils.SIZE, size);

        SearchResults<Company> searchResults;

        try {
            LoggingUtils.getLogger().info("Search started ", logMap);
            searchResults = searchRequestService.getAlphabeticalSearchResults(corporateName, searchBefore, searchAfter,
                    size, requestId);
        } catch (SearchException e) {
            LoggingUtils.getLogger().error("SearchException when searching for company", logMap);
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if(searchResults.getItems() != null) {
            LoggingUtils.getLogger().info("Search successful", logMap);
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LoggingUtils.getLogger().info("No results found", logMap);
        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }
}
