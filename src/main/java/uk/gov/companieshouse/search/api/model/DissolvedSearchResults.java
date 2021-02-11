package uk.gov.companieshouse.search.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DissolvedSearchResults<T> {

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("top_hit")
    private TopHit topHit;

    @JsonProperty("items")
    private List<T> items;

    public DissolvedSearchResults() {
    }

    public DissolvedSearchResults(String etag, TopHit topHit, List<T> items) {
        this.etag = etag;
        this.topHit = topHit;
        this.items = items;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public TopHit getTopHit() {
        return topHit;
    }

    public void setTopHit(TopHit topHit) {
        this.topHit = topHit;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
