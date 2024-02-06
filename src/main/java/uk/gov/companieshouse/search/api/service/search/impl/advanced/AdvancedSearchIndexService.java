package uk.gov.companieshouse.search.api.service.search.impl.advanced;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.NO_RESULTS_FOUND;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.STANDARD_ERROR_MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SUCCESSFUL_SEARCH;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getAdvancedSearchLogMap;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

import java.util.Map;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class AdvancedSearchIndexService {

    private final AdvancedSearchRequestService advancedSearchRequestService;
    private final ConfiguredIndexNamesProvider indices;

    public AdvancedSearchIndexService(AdvancedSearchRequestService advancedSearchRequestService,
        ConfiguredIndexNamesProvider indices) {
        this.advancedSearchRequestService = advancedSearchRequestService;
        this.indices = indices;
    }

    public ResponseObject searchAdvanced(AdvancedSearchQueryParams queryParams, String requestId) {

        Map<String, Object> logMap = getAdvancedSearchLogMap(queryParams, requestId, indices);
        logMap.remove(MESSAGE);

        SearchResults<Company> searchResults;
        try {
            searchResults = advancedSearchRequestService.getSearchResults(queryParams, requestId);
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
}