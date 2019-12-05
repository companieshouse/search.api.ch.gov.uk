package uk.gov.companieshouse.search.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import javax.validation.constraints.NotNull;

public class AlphabeticalSearchRequest {

    @NotNull
    @JsonProperty("company_name")
    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
