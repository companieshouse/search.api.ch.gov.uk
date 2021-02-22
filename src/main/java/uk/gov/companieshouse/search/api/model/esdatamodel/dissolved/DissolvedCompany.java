package uk.gov.companieshouse.search.api.model.esdatamodel.dissolved;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DissolvedCompany {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("company_status")
    private String companyStatus;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("date_of_cessation")
    private String dateOfCessation;

    @JsonProperty("date_of_creation")
    private String dateOfCreation;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("previous_company_names")
    private List<PreviousCompanyName> previousCompanyNames;

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

    public String getDateOfCessation() {
        return dateOfCessation;
    }

    public void setDateOfCessation(String dateOfCessation) {
        this.dateOfCessation = dateOfCessation;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<PreviousCompanyName> getPreviousCompanyNames() {
        return previousCompanyNames;
    }

    public void setPreviousCompanyNames(List<PreviousCompanyName> previousCompanyNames) {
        this.previousCompanyNames = previousCompanyNames;
    }
}
