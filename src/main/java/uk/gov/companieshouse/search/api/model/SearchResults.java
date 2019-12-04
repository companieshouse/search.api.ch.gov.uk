package uk.gov.companieshouse.search.api.model;

import com.google.gson.Gson;

import java.util.List;

public class SearchResults<T> {

    private String searchType;

    private String topHit;

    private List<T> searchResults;

    public SearchResults() {
    }

    public SearchResults(String searchType, String topHit, List<T> searchResults) {
        this.searchType = searchType;
        this.topHit = topHit;
        this.searchResults = searchResults;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getTopHit() {
        return topHit;
    }

    public void setTopHit(String topHit) {
        this.topHit = topHit;
    }

    public List<T> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<T> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
