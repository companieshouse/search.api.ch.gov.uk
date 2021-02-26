package uk.gov.companieshouse.search.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DissolvedTopHit extends TopHit {

    @JsonProperty("date_of_cessation")
    private String dateOfCessation;

    @JsonProperty("date_of_creation")
    private String dateOfCreation;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("previous_company_names")
    private List<PreviousCompanyName> previousCompanyNames;

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
