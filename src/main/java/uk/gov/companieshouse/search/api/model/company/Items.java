package uk.gov.companieshouse.search.api.model.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Items {

    @JsonProperty("company_number")
    private String companyNumber;
    @JsonProperty("company_status")
    private String companyStatus;
    @JsonProperty("corporateName")
    private String corporateName;

    public Items() {}

    public Items(String companyNumber, String companyStatus, String corporateName) {
        this.companyNumber = companyNumber;
        this.companyStatus = companyStatus;
        this.corporateName = corporateName;
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

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}