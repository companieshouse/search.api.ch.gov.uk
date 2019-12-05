package uk.gov.companieshouse.search.api.service.search.impl;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.service.search.SearchRestClientService;

import java.io.IOException;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

@Service
public class SearchRestClientServiceImpl implements SearchRestClientService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResponse searchRestClient(SearchRequest searchRequest) throws IOException {
        return client.search(searchRequest, DEFAULT);
    }
}
