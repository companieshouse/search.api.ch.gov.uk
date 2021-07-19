package uk.gov.companieshouse.search.api.service.rest.impl;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;

import java.io.IOException;

@Service
public class EnhancedSearchRestClientService implements RestClientService {

    @Autowired
    @Qualifier("enhancedClient")
    private RestHighLevelClient enhancedClient;

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        return enhancedClient.search(searchRequest, DEFAULT);
    }

    @Override
    public UpdateResponse upsert(UpdateRequest updateRequest) throws IOException {
        return enhancedClient.update(updateRequest, RequestOptions.DEFAULT);
    }
}
