package uk.gov.companieshouse.search.api.model.esdatamodel.dissolved;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {

    @JsonProperty("locality")
    private String locality;

    @JsonProperty("postal_code")
    private String postalCode;

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
