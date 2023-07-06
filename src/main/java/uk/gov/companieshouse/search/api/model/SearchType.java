package uk.gov.companieshouse.search.api.model;

public class SearchType {
    private final String officerId;
    private final String primarySearchType;

    public SearchType(String officerId, String primarySearchType) {
        this.officerId = officerId;
        this.primarySearchType = primarySearchType;
    }

    public String getOfficerId() {
        return officerId;
    }

    public String getPrimarySearchType() {
        return primarySearchType;
    }
}
