package uk.gov.companieshouse.search.api.service.search;

import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;

public interface SearchRequestService {

    /**
     * Returns alphabetical search results for a company
     *
     * @param searchParam - param to search elastic search database
     * @param requestId - an identifier for the request
     * @return {@link SearchResults}
     */
    DissolvedSearchResults getAlphabeticalSearchResults(String searchParam, String requestId) throws SearchException;
}
