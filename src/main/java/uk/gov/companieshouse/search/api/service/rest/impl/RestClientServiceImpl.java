package uk.gov.companieshouse.search.api.service.rest.impl;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

import java.io.IOException;

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

@Service
public class RestClientServiceImpl implements RestClientService {

    @Autowired
    @Qualifier("alphabeticalClient")
    private RestHighLevelClient alphabeticalClient;
    
    @Autowired
    @Qualifier("dissolvedClient")
    private RestHighLevelClient dissolvedClient;

    @Override
    public SearchResponse searchAlphabeticalRestClient(SearchRequest searchRequest) throws IOException {
        return alphabeticalClient.search(searchRequest, DEFAULT);
    }

    @Override
    public UpdateResponse upsertAlphabeticalRestClient(UpdateRequest updateRequest) throws IOException {
        return alphabeticalClient.update(updateRequest, RequestOptions.DEFAULT);
    }

	@Override
	public SearchResponse searchDissolvedRestClient(SearchRequest searchRequest) throws IOException {
		// TODO Auto-generated method stub
		return dissolvedClient.search(searchRequest, DEFAULT);
	}
}
