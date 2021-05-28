package uk.gov.companieshouse.search.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopHit {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("company_status")
    private String companyStatus;
    
    @JsonProperty("ordered_alpha_key_with_id")
    private String orderedAlphaKeyWithId;

    @JsonProperty("kind")
    private String kind;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getOrderedAlphaKeyWithId() {
        return orderedAlphaKeyWithId;
    }

    public void setOrderedAlphaKeyWithId(String orderedAlphaKeyWithId) {
        this.orderedAlphaKeyWithId = orderedAlphaKeyWithId;
    }
}
