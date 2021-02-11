package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class DissolvedSearchIndexService {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private static final String DISSOLVED_SEARCH = "Dissolved search: ";

    public DissolvedResponseObject search(String companyName, String requestId) {

        DissolvedSearchResults searchResults = new DissolvedSearchResults();
        searchResults.setEtag("TEST");
        TopHit topHit = new TopHit();
        topHit.setCompanyName("TEST COMPANY NAME");
        topHit.setCompanyNumber("TEST COMPANY NUMBER");
        searchResults.setTopHit(topHit);
        searchResults.setItems(null);

        LOG.info(DISSOLVED_SEARCH + "successful for: " + companyName);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
    }
}
