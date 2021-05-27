package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseCompany {
    
    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("company_status")
    private String companyStatus;
    
    @JsonProperty("ordered_alpha_key")
    private String orderedAlphaKey;

    @JsonProperty("ordered_alpha_key_with_id")
    private String orderedAlphaKeyWithId;

    @JsonProperty("record_type")
    private String recordType;
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

    public String getOrderedAlphaKey() {
        return orderedAlphaKey;
    }

    public void setOrderedAlphaKey(String orderedAlphaKey) {
        this.orderedAlphaKey = orderedAlphaKey;
    }

    public String getOrderedAlphaKeyWithId() {
        return orderedAlphaKeyWithId;
    }

    public void setOrderedAlphaKeyWithId(String orderedAlphaKeyWithId) {
        this.orderedAlphaKeyWithId = orderedAlphaKeyWithId;
    }
    
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
