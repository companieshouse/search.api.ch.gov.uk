package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DissolvedCompany extends BaseCompany {

    @JsonProperty("date_of_cessation")
    private LocalDate dateOfCessation;

    @JsonProperty("date_of_creation")
    private LocalDate dateOfCreation;

    @JsonProperty("registered_office_address")
    private Address registeredOfficeAddress;

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
        return registeredOfficeAddress;
    }

    public void setAddress(Address registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public List<PreviousCompanyName> getPreviousCompanyNames() {
        return previousCompanyNames;
    }

    public void setPreviousCompanyNames(List<PreviousCompanyName> previousCompanyNames) {
        this.previousCompanyNames = previousCompanyNames;
    }

    @Override
    public String toString() {
        return "DissolvedCompany{" +
                "dateOfCessation=" + dateOfCessation +
                ", dateOfCreation=" + dateOfCreation +
                ", registeredOfficeAddress=" + registeredOfficeAddress +
                ", previousCompanyNames=" + previousCompanyNames +
                '}';
    }
}
