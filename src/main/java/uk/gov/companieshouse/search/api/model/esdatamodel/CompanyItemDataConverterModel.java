package uk.gov.companieshouse.search.api.model.esdatamodel;

import java.time.LocalDate;
import java.util.Objects;

public class CompanyItemDataConverterModel {

    private String fullAddress;

    private String companyName;

    private LocalDate dateOfCreation;

    private LocalDate ceasedOn;

    public String getFullAddress() {
        return fullAddress;
    }

    public String getCompanyName() {
        return companyName;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public LocalDate getCeasedOn() {
        return ceasedOn;
    }

    public CompanyItemDataConverterModel fullAddress(
            String fullAddress) {
        this.fullAddress = fullAddress;
        return this;
    }

    public CompanyItemDataConverterModel companyName(
            String companyName) {
        this.companyName = companyName;
        return this;
    }

    public CompanyItemDataConverterModel dateOfCreation(
            LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
        return this;
    }

    public CompanyItemDataConverterModel ceasedOn(
            LocalDate ceasedOn) {
        this.ceasedOn = ceasedOn;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanyItemDataConverterModel that = (CompanyItemDataConverterModel) o;
        return Objects.equals(fullAddress, that.fullAddress) && Objects.equals(companyName,
                that.companyName) && Objects.equals(dateOfCreation, that.dateOfCreation)
                && Objects.equals(ceasedOn, that.ceasedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullAddress, companyName, dateOfCreation, ceasedOn);
    }
}
