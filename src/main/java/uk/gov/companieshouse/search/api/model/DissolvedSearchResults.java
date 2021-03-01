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

    @JsonProperty("kind")
    private String kind;

    public DissolvedSearchResults() {
    }

    public DissolvedSearchResults(String etag, TopHit topHit, List<T> items, String kind) {
        this.etag = etag;
        this.topHit = topHit;
        this.items = items;
        this.kind = kind;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
