package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.Map;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class AlphabeticalSearchIndexService implements SearchIndexService {

    private final SearchRequestService<Company> searchRequestService;
    private final ConfiguredIndexNamesProvider indices;

    public AlphabeticalSearchIndexService(SearchRequestService<Company> searchRequestService,
        ConfiguredIndexNamesProvider indices) {
        this.searchRequestService = searchRequestService;
        this.indices = indices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseObject search(String corporateName, String searchBefore, String searchAfter, Integer size,
            String requestId) {


        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .companyName(corporateName)
                .indexName(indices.alphabetical())
                .searchBefore(searchBefore)
                .searchAfter(searchAfter)
                .size(String.valueOf(size))
                .build().getLogMap();

        SearchResults<Company> searchResults;

        try {
            getLogger().info("Search started ", logMap);
            searchResults = searchRequestService.getAlphabeticalSearchResults(corporateName, searchBefore, searchAfter,
                    size, requestId);
        } catch (SearchException e) {
            getLogger().error("SearchException when searching for company", logMap);
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if(searchResults.getItems() != null && !searchResults.getItems().isEmpty()) {
            getLogger().info("Search successful", logMap);
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        getLogger().info("No results found", logMap);
        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }
}
