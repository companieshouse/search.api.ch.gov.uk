package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class CompanySearchItem {

    private static final String RECORD_TYPE = "companies";

    @JsonProperty("corporate_name_start")
    private final String corporateNameStart;

    @JsonProperty("corporate_name_ending")
    private final String corporateNameEnding;

    @JsonProperty("ceased_on")
    private final LocalDate ceasedOn;

    @JsonProperty("date_of_creation")
    private final LocalDate dateOfCreation;

    @JsonProperty("full_address")
    private final String fullAddress;

    @JsonProperty("record_type")
    private final String recordType;

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

    private CompanySearchItem(Builder builder) {
        corporateNameStart = builder.corporateNameStart;
        corporateNameEnding = builder.corporateNameEnding;
        ceasedOn = builder.ceasedOn;
        dateOfCreation = builder.dateOfCreation;
        fullAddress = builder.fullAddress;
        recordType = builder.recordType;
        address = builder.address;
        companyNumber = builder.companyNumber;
        externalRegistrationNumber = builder.externalRegistrationNumber;
        dateOfCessation = builder.dateOfCessation;
        sicCodes = builder.sicCodes;
        companyStatus = builder.companyStatus;
        sameAsKey = builder.sameAsKey;
        wildcardKey = builder.wildcardKey;
    }

    public String getCorporateNameStart() {
        return corporateNameStart;
    }

    public String getCorporateNameEnding() {
        return corporateNameEnding;
    }

    public LocalDate getCeasedOn() {
        return ceasedOn;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getRecordType() {
        return recordType;
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

        private String corporateNameStart;

        private String corporateNameEnding;

        private LocalDate ceasedOn;

        private LocalDate dateOfCreation;

        private String fullAddress;

        private String recordType;
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

        private Builder() {
            this.recordType = RECORD_TYPE;
        }

        public Builder corporateNameStart(String corporateNameStart) {
            this.corporateNameStart = corporateNameStart;
            return this;
        }

        public Builder corporateNameEnding(String corporateNameEnding) {
            this.corporateNameEnding = corporateNameEnding;
            return this;
        }

        public Builder ceasedOn(LocalDate ceasedOn) {
            this.ceasedOn = ceasedOn;
            return this;
        }

        public Builder dateOfCreation(LocalDate dateOfCreation) {
            this.dateOfCreation = dateOfCreation;
            return this;
        }

        public Builder fullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
            return this;
        }

        public Builder recordType(String recordType) {
            this.recordType = recordType;
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

        public CompanySearchItem build() {
            return new CompanySearchItem(this);
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
        CompanySearchItem that = (CompanySearchItem) o;
        return Objects.equals(corporateNameStart, that.corporateNameStart) && Objects.equals(
                corporateNameEnding, that.corporateNameEnding) && Objects.equals(ceasedOn, that.ceasedOn)
                && Objects.equals(dateOfCreation, that.dateOfCreation) && Objects.equals(fullAddress,
                that.fullAddress) && Objects.equals(recordType, that.recordType) && Objects.equals(
                address, that.address) && Objects.equals(companyNumber, that.companyNumber)
                && Objects.equals(externalRegistrationNumber, that.externalRegistrationNumber)
                && Objects.equals(dateOfCessation, that.dateOfCessation) && Objects.equals(sicCodes,
                that.sicCodes) && Objects.equals(companyStatus, that.companyStatus) && Objects.equals(
                sameAsKey, that.sameAsKey) && Objects.equals(wildcardKey, that.wildcardKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corporateNameStart, corporateNameEnding, ceasedOn, dateOfCreation, fullAddress, recordType,
                address, companyNumber, externalRegistrationNumber, dateOfCessation, sicCodes, companyStatus, sameAsKey,
                wildcardKey);
    }
}
