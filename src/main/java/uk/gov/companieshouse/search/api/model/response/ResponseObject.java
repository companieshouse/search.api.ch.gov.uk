package uk.gov.companieshouse.search.api.model.response;

import com.google.gson.Gson;
import uk.gov.companieshouse.search.api.model.SearchResults;

public class ResponseObject<T> {

    private ResponseStatus status;
    private SearchResults<T> searchResults;

    public ResponseObject(ResponseStatus status) {
        this.status = status;
    }

    public ResponseObject(ResponseStatus status, SearchResults<T> searchResults) {
        this.status = status;
        this.searchResults = searchResults;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public SearchResults<T> getData() {
        return searchResults;
    }

    public void setData(SearchResults<T> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}