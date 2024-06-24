package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class CompanySearchItemFullData {

    private final CompanySearchItemData companySearchItemData;

    @JsonProperty("address")
    private final CompanySearchAddress address;

    @JsonProperty("company_number")
    private final String companyNumber;

    @JsonProperty("external_registration_number")
    private final String externalRegistrationNumber;

    @JsonProperty("date_of_cessation")
    private final LocalDate dateOfCessation;

    @JsonProperty("sic_codes")
    private final List<String> sicCodes;

    @JsonProperty("company_status")
    private final String companyStatus;

    @JsonProperty("same_as_key")
    private final String sameAsKey;

    @JsonProperty("wildcard_key")
    private final String wildcardKey;

    private CompanySearchItemFullData(Builder builder) {
        companySearchItemData = builder.companySearchItemData;
        address = builder.address;
        companyNumber = builder.companyNumber;
        externalRegistrationNumber = builder.externalRegistrationNumber;
        dateOfCessation = builder.dateOfCessation;
        sicCodes = builder.sicCodes;
        companyStatus = builder.companyStatus;
        sameAsKey = builder.sameAsKey;
        wildcardKey = builder.wildcardKey;
    }

    public CompanySearchItemData getCompanySearchItemData() {
        return companySearchItemData;
    }

    public CompanySearchAddress getAddress() {
        return address;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getExternalRegistrationNumber() {
        return externalRegistrationNumber;
    }

    public LocalDate getDateOfCessation() {
        return dateOfCessation;
    }

    public List<String> getSicCodes() {
        return sicCodes;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public String getSameAsKey() {
        return sameAsKey;
    }

    public String getWildcardKey() {
        return wildcardKey;
    }

    public static final class Builder {

        private CompanySearchItemData companySearchItemData;

        private CompanySearchAddress address;

        private String companyNumber;

        private String externalRegistrationNumber;

        private LocalDate dateOfCessation;

        private List<String> sicCodes;

        private String companyStatus;

        private String sameAsKey;

        private String wildcardKey;

        public static Builder builder() {
            return new Builder();
        }

        private Builder() { }

        public Builder companySearchData(CompanySearchItemData companySearchItemData) {
            this.companySearchItemData = companySearchItemData;
            return this;
        }

        public Builder address(CompanySearchAddress address) {
            this.address = address;
            return this;
        }

        public Builder companyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public Builder externalRegistrationNumber(String externalRegistrationNumber) {
            this.externalRegistrationNumber = externalRegistrationNumber;
            return this;
        }

        public Builder dateOfCessation(LocalDate dateOfCessation) {
            this.dateOfCessation = dateOfCessation;
            return this;
        }

        public Builder sicCodes(List<String> sicCodes) {
            this.sicCodes = sicCodes;
            return this;
        }

        public Builder companyStatus(String companyStatus) {
            this.companyStatus = companyStatus;
            return this;
        }

        public Builder sameAsKey(String sameAsKey) {
            this.sameAsKey = sameAsKey;
            return this;
        }

        public Builder wildcardKey(String wildcardKey) {
            this.wildcardKey = wildcardKey;
            return this;
        }

        public CompanySearchItemFullData build() {
            return new CompanySearchItemFullData(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanySearchItemFullData that = (CompanySearchItemFullData) o;
        return Objects.equals(companySearchItemData, that.companySearchItemData) && Objects.equals(
                address, that.address) && Objects.equals(companyNumber, that.companyNumber)
                && Objects.equals(externalRegistrationNumber, that.externalRegistrationNumber)
                && Objects.equals(dateOfCessation, that.dateOfCessation) && Objects.equals(sicCodes,
                that.sicCodes) && Objects.equals(companyStatus, that.companyStatus) && Objects.equals(
                sameAsKey, that.sameAsKey) && Objects.equals(wildcardKey, that.wildcardKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companySearchItemData, address, companyNumber, externalRegistrationNumber, dateOfCessation,
                sicCodes, companyStatus, sameAsKey, wildcardKey);
    }
}
