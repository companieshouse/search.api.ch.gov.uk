package uk.gov.companieshouse.search.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import org.springframework.lang.NonNull;

public class AlphabeticalSearchRequest {

    @NonNull
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
