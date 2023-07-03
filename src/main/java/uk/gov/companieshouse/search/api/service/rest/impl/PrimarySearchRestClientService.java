package uk.gov.companieshouse.search.api.service.rest.impl;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;

import java.io.IOException;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

@Service
public class PrimarySearchRestClientService implements RestClientService {

    @Autowired
    @Qualifier("primaryClient")
    private RestHighLevelClient primaryClient;

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        return primaryClient.search(searchRequest, DEFAULT);
    }

    @Override
    public UpdateResponse upsert(UpdateRequest updateRequest) throws IOException {
        return primaryClient.update(updateRequest, DEFAULT);
    }

    public DeleteResponse delete(DeleteRequest deleteRequest) throws IOException {
        return primaryClient.delete(deleteRequest, DEFAULT);
    }
}