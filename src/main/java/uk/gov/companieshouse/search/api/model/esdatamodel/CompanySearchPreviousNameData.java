package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Objects;

public class CompanySearchPreviousNameData {

    private static final String RECORD_TYPE = "companies";

    @JsonProperty("corporate_name_start")
    private final String corporateNameStart;

    @JsonProperty("corporate_name_ending")
    private final String corporateNameEnding;

    @JsonProperty("ceased_on")
    private final LocalDate ceasedOn;

    @JsonProperty("full_address")
    private final String fullAddress;

    @JsonProperty("record_type")
    private final String recordType;

    private CompanySearchPreviousNameData(Builder builder) {
        corporateNameStart = builder.corporateNameStart;
        corporateNameEnding = builder.corporateNameEnding;
        ceasedOn = builder.ceasedOn;
        fullAddress = builder.fullAddress;
        recordType = builder.recordType;
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

    public String getFullAddress() {
        return fullAddress;
    }

    public String getRecordType() {
        return recordType;
    }

    public static final class Builder {

        private String corporateNameStart;

        private String corporateNameEnding;

        private LocalDate ceasedOn;

        private String fullAddress;

        private String recordType;


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

        public Builder fullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
            return this;
        }

        public Builder recordType(String recordType) {
            this.recordType = recordType;
            return this;
        }


        public CompanySearchPreviousNameData build() {
            return new CompanySearchPreviousNameData(this);
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
        CompanySearchPreviousNameData that = (CompanySearchPreviousNameData) o;
        return Objects.equals(corporateNameStart, that.corporateNameStart) && Objects.equals(
                corporateNameEnding, that.corporateNameEnding) && Objects.equals(ceasedOn, that.ceasedOn)
                && Objects.equals(fullAddress, that.fullAddress) && Objects.equals(recordType,
                that.recordType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corporateNameStart, corporateNameEnding, ceasedOn, fullAddress, recordType);
    }
}
