package uk.gov.companieshouse.search.api.service.rest;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;

import java.io.IOException;

public interface RestClientService {

    /**
     * interface for elastic search high level rest client used for search
     *
     * @param searchRequest - searchRequest containing search parameters
     * @return SearchResponse - response from elastic search db
     */
    SearchResponse searchRestClient(SearchRequest searchRequest) throws IOException;

    /**
     * interface for elastic search high level rest client used in upsert
     *
     * @param updateRequest - updateRequest containing update parameters
     * @return UpdateResponse - response from elastic search db
     */
    UpdateResponse upsert(UpdateRequest updateRequest) throws IOException;
}
