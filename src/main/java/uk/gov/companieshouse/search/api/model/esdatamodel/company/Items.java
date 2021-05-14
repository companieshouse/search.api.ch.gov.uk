package uk.gov.companieshouse.search.api.model.esdatamodel.company;

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

    @JsonProperty("corporate_name")
    private String corporateName;
    
    @JsonProperty("ordered_alpha_key")
    private String orderedAlphaKey;

    @JsonProperty("record_type")
    private String recordType;

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

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getOrderedAlphaKey() {
        return orderedAlphaKey;
    }

    public void setOrderedAlphaKey(String orderedAlphaKey) {
        this.orderedAlphaKey = orderedAlphaKey;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}