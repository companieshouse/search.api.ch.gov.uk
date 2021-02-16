package uk.gov.companieshouse.search.api.elasticsearch;

import org.springframework.beans.factory.annotation.Autowired;

import uk.gov.companieshouse.search.api.service.rest.RestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.DissolvedSearchRestClientService;

public class DissolvedSearchRequests extends AbstractSearchRequest {
	
	@Autowired
    private DissolvedSearchRestClientService searchRestClient;

    @Autowired
    private DissolvedSearchQueries searchQueries;
	
	private static final String INDEX = "DISSOLVED_SEARCH_INDEX";
    private static final String RESULTS_SIZE = "DISSOLVED_SEARCH_RESULT_MAX";

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
		return searchQueries;
	}

}
