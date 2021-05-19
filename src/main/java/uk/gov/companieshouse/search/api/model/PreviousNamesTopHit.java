package uk.gov.companieshouse.search.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.companieshouse.search.api.model.esdatamodel.Address;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreviousNamesTopHit extends TopHit{

    @JsonProperty("previous_company_name")
    private String previousCompanyName;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("date_of_cessation")
    private LocalDate dateOfCessation;

    @JsonProperty("date_of_creation")
    private LocalDate dateOfCreation;

    public String getPreviousCompanyName() {
        return previousCompanyName;
    }
    public void setPreviousCompanyName(String previousCompanyName) {
        this.previousCompanyName = previousCompanyName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public LocalDate getDateOfCessation() {
        return dateOfCessation;
    }

    public void setDateOfCessation(LocalDate dateOfCessation) {
        this.dateOfCessation = dateOfCessation;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }
}
