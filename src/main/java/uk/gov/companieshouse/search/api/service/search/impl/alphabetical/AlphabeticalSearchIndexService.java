package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

import java.util.Map;

@Service
public class AlphabeticalSearchIndexService implements SearchIndexService {

    @Autowired
    private SearchRequestService searchRequestService;

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private static final String ALPHABETICAL_SEARCH = "Alphabetical Search: ";

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseObject search(String corporateName, String requestId) {
        
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, corporateName);

        SearchResults searchResults;

        try {
            LOG.info(ALPHABETICAL_SEARCH + "started ", logMap);
            searchResults = searchRequestService.getAlphabeticalSearchResults(corporateName, requestId);
        } catch (SearchException e) {
            LOG.error("An error occurred in alphabetical search whilst searching: " + corporateName, e);
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if(searchResults.getResults() != null) {
            LOG.info(ALPHABETICAL_SEARCH + "successful", logMap);
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LOG.info(ALPHABETICAL_SEARCH + "No results found", logMap);
        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }
}

