package uk.gov.companieshouse.search.api.service.rest;


import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;

import java.io.IOException;

public interface OpenSearchRestClientService {

    /**
     * interface for Open search high level rest client used for search
     *
     * @param searchRequest - searchRequest containing search parameters
     * @return SearchResponse - response from Open search db
     */
    SearchResponse<Object> search(SearchRequest searchRequest) throws IOException;

}
