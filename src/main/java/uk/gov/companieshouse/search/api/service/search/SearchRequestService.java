package uk.gov.companieshouse.search.api.service.search;

import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;

public interface SearchRequestService {

    /**
     * Returns alphabetical search results for a company
     * @param searchParam   - param to search elastic search database
     * @param searchBefore  - param to search previous results
     * @param searchAfter   - param to search following results
     * @param size          - param to limit the number of results
     * @param requestId     - an identifier for the request
     * @return {@link SearchResults}
     * @throws SearchException
     */
    SearchResults getAlphabeticalSearchResults(String searchParam, String searchBefore, String searchAfter,
            Integer size, String requestId) throws SearchException;
}
