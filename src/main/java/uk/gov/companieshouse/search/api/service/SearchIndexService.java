package uk.gov.companieshouse.search.api.service;

import uk.gov.companieshouse.search.api.model.response.ResponseObject;

public interface SearchIndexService {

    /**
     * Search Elastic search data base using search Param.
     *
     * @param searchParam - Value to search elastc search database with.
     * @return {@link ResponseObject}
     */
    ResponseObject search(String searchParam);
}
