package uk.gov.companieshouse.search.api.service.search;

import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;

public interface SearchRequestService {

    /**
     * Returns alphabetical search results for a company
     *
     * @param searchParam - param to search elastic search database
     * @param requestId   - an identifier for the request
     * @return {@link SearchResults}
     */
    SearchResults getAlphabeticalSearchResults(String searchParam, String requestId) throws SearchException;

    SearchResults getAlphabeticalSearchResults(String searchParam, String searchBefore, String searchAfter,
            Integer size, String requestId) throws SearchException;
}
