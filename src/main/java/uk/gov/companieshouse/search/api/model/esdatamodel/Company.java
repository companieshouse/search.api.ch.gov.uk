package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Company extends BaseCompany{
    
    @JsonProperty("record_type")
    private String recordType;

    @JsonProperty("links")
    private Links links;
    
    @JsonProperty("company_type")
    private String companyType;

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
}
