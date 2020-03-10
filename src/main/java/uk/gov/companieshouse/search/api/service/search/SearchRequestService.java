package uk.gov.companieshouse.search.api.service.search;

import org.elasticsearch.action.search.SearchRequest;

import java.io.IOException;

public interface SearchRequestService {

    /**
     * Creates search request to use in Elastic search
     *
     * @param searchParam - param to search elastic search database
     * @return {@link SearchRequest}
     */
    SearchRequest createSearchRequest(String searchParam, String requestId) throws IOException;
}
