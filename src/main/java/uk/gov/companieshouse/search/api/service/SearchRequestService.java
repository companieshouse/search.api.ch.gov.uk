package uk.gov.companieshouse.search.api.service;

import org.elasticsearch.action.search.SearchRequest;

public interface SearchRequestService {

    /**
     * Creates search request to use in Elastic search
     *
     * @param searchParam - param to search elastic search database
     * @param searchIndexFrom - index for pagination
     * @return {@link SearchRequest}
     */
    SearchRequest createSearchRequest(String searchParam, int searchIndexFrom);
}
