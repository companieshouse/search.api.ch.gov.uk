package uk.gov.companieshouse.search.api.service.search.impl.enhanced;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

@Service
public class EnhancedSearchIndexService {

    @Autowired
    private EnhancedSearchRequestService enhancedSearchRequestService;

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private static final String ENHANCED_SEARCH = "Enhanced search: ";

    public ResponseObject searchEnhanced(String companyName) {

        SearchResults<Company> searchResults;

        searchResults = enhancedSearchRequestService.getSearchResults(companyName);

        LOG.info(ENHANCED_SEARCH + "successful for: " + companyName);
        return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
    }
}