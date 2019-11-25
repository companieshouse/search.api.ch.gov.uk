package uk.gov.companieshouse.search.api.model.response;

import com.google.gson.Gson;
import uk.gov.companieshouse.search.api.model.SearchResults;

public class ResponseObject {

    private ResponseStatus status;

    private SearchResults searchResults;

    public ResponseObject(ResponseStatus status) {
        this.status = status;
    }

    public ResponseObject(ResponseStatus status, SearchResults searchResults) {
        this.status = status;
        this.searchResults = searchResults;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public SearchResults getData() {
        return searchResults;
    }

    public void setData(SearchResults searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
