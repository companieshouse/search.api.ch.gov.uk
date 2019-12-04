package uk.gov.companieshouse.search.api.service.search;

import org.elasticsearch.action.search.SearchResponse;
import uk.gov.companieshouse.search.api.exception.SearchException;

public interface SearchRestClientService {

    /**
     * interface for elastic search high level rest client
     *
     * @param searchParam - term used for search
     * @return SearchResponse - response from elastic search db
     */
    SearchResponse searchRestClient(String searchParam) throws SearchException;
}
