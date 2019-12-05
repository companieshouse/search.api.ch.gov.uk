package uk.gov.companieshouse.search.api.service.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

import java.io.IOException;

public interface SearchRestClientService {

    /**
     * interface for elastic search high level rest client
     *
     * @param searchRequest - searchRequest containing search parameters
     * @return SearchResponse - response from elastic search db
     */
    SearchResponse searchRestClient(SearchRequest searchRequest) throws IOException;
}
