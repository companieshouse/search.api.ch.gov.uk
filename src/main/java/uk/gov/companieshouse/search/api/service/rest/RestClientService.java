package uk.gov.companieshouse.search.api.service.rest;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

@Service
public class RestClientService {

    @Autowired
    @Qualifier("alphabeticalClient")
    private RestHighLevelClient alphabeticalClient;
    
    @Autowired
    @Qualifier("dissolvedClient")
    private RestHighLevelClient dissolvedClient;

    /**
     * interface for elastic search high level rest client used for alphabetical search
     *
     * @param searchRequest - searchRequest containing search parameters
     * @return SearchResponse - response from elastic search db
     */
    public SearchResponse searchAlphabeticalRestClient(SearchRequest searchRequest) throws IOException {
        return alphabeticalClient.search(searchRequest, DEFAULT);
    }

    /**
     * interface for elastic search high level rest client used in upserting documents to alphabetical search
     *
     * @param updateRequest - updateRequest containing update parameters
     * @return UpdateResponse - response from elastic search db
     */
    public UpdateResponse upsertAlphabeticalRestClient(UpdateRequest updateRequest) throws IOException {
        return alphabeticalClient.update(updateRequest, RequestOptions.DEFAULT);
    }

    /**
     * interface for elastic search high level rest client used for dissolved search
     *
     * @param searchRequest - searchRequest containing search parameters
     * @return SearchResponse - response from elastic search db
     */
	public SearchResponse searchDissolvedRestClient(SearchRequest searchRequest) throws IOException {
		// TODO Auto-generated method stub
		return dissolvedClient.search(searchRequest, DEFAULT);
	}
}
