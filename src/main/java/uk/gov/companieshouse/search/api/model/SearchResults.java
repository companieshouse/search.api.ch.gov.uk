package uk.gov.companieshouse.search.api.model;

import com.google.gson.Gson;

import java.util.List;

public class SearchResults<T> {

    private String searchType;

    private String topHit;

    private List<T> results;

    public SearchResults() {
    }

    public SearchResults(String searchType, String topHit, List<T> results) {
        this.searchType = searchType;
        this.topHit = topHit;
        this.results = results;
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

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
