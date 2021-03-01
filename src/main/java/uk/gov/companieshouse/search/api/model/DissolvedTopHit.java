package uk.gov.companieshouse.search.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;

import java.time.LocalDate;
import java.util.List;

public class DissolvedTopHit extends TopHit {

    @JsonProperty("date_of_cessation")
    private LocalDate dateOfCessation;

    @JsonProperty("date_of_creation")
    private LocalDate dateOfCreation;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("previous_company_names")
    private List<PreviousCompanyName> previousCompanyNames;

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
