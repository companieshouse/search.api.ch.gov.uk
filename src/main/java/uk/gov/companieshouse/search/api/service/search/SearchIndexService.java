package uk.gov.companieshouse.search.api.service.search;

import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;

public interface SearchIndexService {

    /**
     * Search Elastic search data base using search Param.
     *
     * @param searchParam - Value to search elastc search database with.
     * @return {@link ResponseObject}
     */    
    DissolvedResponseObject search(String searchParam, String searchBefore, String searchAfter, Integer size, String requestId);

}
