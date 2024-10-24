package uk.gov.companieshouse.search.api.model.esdatamodel;

import java.time.LocalDate;
import java.util.List;

import java.util.Objects;
import uk.gov.companieshouse.api.company.RegisteredOfficeAddress;

public class CompanySearchItemConverterModel {

    private boolean partialData;

    private String companyName;

    private String companyNumber;

    private String companyStatus;

    private String externalRegistrationNumber;

    private List<String> sicCodes;

    private RegisteredOfficeAddress registeredOfficeAddress;

    private LocalDate ceasedOn;

    private LocalDate dateOfCessation;

    private LocalDate dateOfCreation;

    private String alphaKey;

    private String sameAsKey;

    public boolean isPartialData() {
        return partialData;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public String getExternalRegistrationNumber() {
        return externalRegistrationNumber;
    }

    public List<String> getSicCodes() {
        return sicCodes;
    }

    public RegisteredOfficeAddress getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public LocalDate getCeasedOn() {
        return ceasedOn;
    }

    public LocalDate getDateOfCessation() {
        return dateOfCessation;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public String getAlphaKey() {
        return alphaKey;
    }

    public String getSameAsKey() {
        return sameAsKey;
    }

    public CompanySearchItemConverterModel partialData(boolean partialData) {
        this.partialData = partialData;
        return this;
    }

    public CompanySearchItemConverterModel companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public CompanySearchItemConverterModel companyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
        return this;
    }

    public CompanySearchItemConverterModel companyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
        return this;
    }

    public CompanySearchItemConverterModel externalRegistrationNumber(String externalRegistrationNumber) {
        this.externalRegistrationNumber = externalRegistrationNumber;
        return this;
    }

    public CompanySearchItemConverterModel sicCodes(List<String> sicCodes) {
        this.sicCodes = sicCodes;
        return this;
    }

    public CompanySearchItemConverterModel registeredOfficeAddress(RegisteredOfficeAddress registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
        return this;
    }

    public CompanySearchItemConverterModel ceasedOn(LocalDate ceasedOn) {
        this.ceasedOn = ceasedOn;
        return this;
    }

    public CompanySearchItemConverterModel dateOfCessation(LocalDate dateOfCessation) {
        this.dateOfCessation = dateOfCessation;
        return this;
    }

    public CompanySearchItemConverterModel dateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
        return this;
    }

    public CompanySearchItemConverterModel alphaKey(String alphaKey) {
        this.alphaKey = alphaKey;
        return this;
    }

    public CompanySearchItemConverterModel sameAsKey(String sameAsKey) {
        this.sameAsKey = sameAsKey;
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
        CompanySearchItemConverterModel that = (CompanySearchItemConverterModel) o;
        return partialData == that.partialData && Objects.equals(companyName, that.companyName)
                && Objects.equals(companyNumber, that.companyNumber) && Objects.equals(companyStatus,
                that.companyStatus) && Objects.equals(externalRegistrationNumber, that.externalRegistrationNumber)
                && Objects.equals(sicCodes, that.sicCodes) && Objects.equals(registeredOfficeAddress,
                that.registeredOfficeAddress) && Objects.equals(ceasedOn, that.ceasedOn)
                && Objects.equals(dateOfCessation, that.dateOfCessation) && Objects.equals(
                dateOfCreation, that.dateOfCreation) && Objects.equals(alphaKey, that.alphaKey)
                && Objects.equals(sameAsKey, that.sameAsKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partialData, companyName, companyNumber, companyStatus, externalRegistrationNumber,
                sicCodes,
                registeredOfficeAddress, ceasedOn, dateOfCessation, dateOfCreation, alphaKey, sameAsKey);
    }
}
