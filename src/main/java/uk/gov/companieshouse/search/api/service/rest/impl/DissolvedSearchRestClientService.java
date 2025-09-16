package uk.gov.companieshouse.search.api.service.rest.impl;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

import java.io.IOException;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;

@Service
public class DissolvedSearchRestClientService implements RestClientService {
    
    private RestHighLevelClient dissolvedClient;

    public DissolvedSearchRestClientService(
            @Qualifier("dissolvedRestClient") RestHighLevelClient dissolvedClient
    ) {
        this.dissolvedClient = dissolvedClient;
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        return dissolvedClient.search(searchRequest, DEFAULT);
    }

    @Override
    public UpdateResponse upsert(UpdateRequest updateRequest) throws IOException {
        return dissolvedClient.update(updateRequest, RequestOptions.DEFAULT);
    }
}
