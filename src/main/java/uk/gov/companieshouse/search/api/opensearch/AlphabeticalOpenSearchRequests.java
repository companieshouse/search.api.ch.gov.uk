package uk.gov.companieshouse.search.api.opensearch;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.service.rest.OpenSearchRestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalOpenSearchRestClientService;

@Component
public class AlphabeticalOpenSearchRequests extends AbstractOpenSearchRequest {

    private final AlphabeticalOpenSearchRestClientService searchRestClient;
    private final AlphabeticalOpenSearchQueries alphabeticalSearchQueries;

    private static final String INDEX = "ALPHABETICAL_OPEN_SEARCH_INDEX";
    private static final String RESULTS_SIZE = "ALPHABETICAL_SEARCH_RESULT_MAX";

    public AlphabeticalOpenSearchRequests(
            EnvironmentReader environmentReader,
            AlphabeticalOpenSearchRestClientService searchRestClient,
            AlphabeticalOpenSearchQueries alphabeticalSearchQueries
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
    OpenSearchRestClientService getRestClientService() {
        return searchRestClient;
    }

    @Override
    AbstractOpenSearchQuery getSearchQuery() {
        return alphabeticalSearchQueries;
    }
}
