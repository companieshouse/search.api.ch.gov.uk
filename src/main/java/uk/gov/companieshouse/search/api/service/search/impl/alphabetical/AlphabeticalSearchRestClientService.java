package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;
import uk.gov.companieshouse.search.api.service.search.SearchRestClientService;

import java.io.IOException;

import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class AlphabeticalSearchRestClientService implements SearchRestClientService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private SearchRequestService searchRequestService;

    private static final String ALPHABETICAL_SEARCH = "Alphabetical Search: ";

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Override
    public SearchResponse searchRestClient(String corporateName) throws SearchException {

        try {
            return client.search(
                searchRequestService.createSearchRequest(corporateName), DEFAULT);
        } catch (IOException e) {
            LOG.error(ALPHABETICAL_SEARCH + "Failed to get a search response from elastic search " +
                "for: " + corporateName, e);
            throw new SearchException("Error occurred while searching index", e);
        }
    }
}
