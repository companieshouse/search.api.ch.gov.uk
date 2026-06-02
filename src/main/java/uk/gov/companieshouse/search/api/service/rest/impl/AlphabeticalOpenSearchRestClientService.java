package uk.gov.companieshouse.search.api.service.rest.impl;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.service.rest.OpenSearchRestClientService;

import java.io.IOException;


@Service
public class AlphabeticalOpenSearchRestClientService implements OpenSearchRestClientService {

    private final OpenSearchClient alphabeticalOpenSearchClient;

    public AlphabeticalOpenSearchRestClientService(
            OpenSearchClient alphabeticalOpenSearchClient
    ) {
        this.alphabeticalOpenSearchClient = alphabeticalOpenSearchClient;
    }

    @Override
    public SearchResponse<Object> search(SearchRequest searchRequest) throws IOException {
        return alphabeticalOpenSearchClient.search(searchRequest, Object.class);
    }

}
