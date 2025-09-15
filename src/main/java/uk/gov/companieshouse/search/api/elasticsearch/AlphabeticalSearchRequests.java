package uk.gov.companieshouse.search.api.elasticsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;

@Component
public class AlphabeticalSearchRequests extends AbstractSearchRequest {

    private AlphabeticalSearchRestClientService searchRestClient;

    private AlphabeticalSearchQueries alphabeticalSearchQueries;

    private static final String INDEX = "ALPHABETICAL_SEARCH_INDEX";
    private static final String RESULTS_SIZE = "ALPHABETICAL_SEARCH_RESULT_MAX";

    @Autowired
    public AlphabeticalSearchRequests(
        EnvironmentReader environmentReader,
        AlphabeticalSearchRestClientService searchRestClient,
        AlphabeticalSearchQueries alphabeticalSearchQueries
    ) {
        super(environmentReader);
        this.searchRestClient = searchRestClient;
        this.alphabeticalSearchQueries = alphabeticalSearchQueries;
    }

    @Override
    String getIndex() {
        return INDEX;
    }
    
    @Override
    String getResultsSize() {
        return RESULTS_SIZE;
    }
    
    @Override
    RestClientService getRestClientService() {
        return searchRestClient;
    }
    
    @Override
    AbstractSearchQuery getSearchQuery() {
        return alphabeticalSearchQueries;
    }
}
