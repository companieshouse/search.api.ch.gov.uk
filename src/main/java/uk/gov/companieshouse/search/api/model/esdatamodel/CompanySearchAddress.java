package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class CompanySearchAddress {

    @JsonProperty("address_line_1")
    private final String addressLine1;

    @JsonProperty("address_line_2")
    private final String addressLine2;

    @JsonProperty("care_of")
    private final String careOf;

    @JsonProperty("country")
    private final String country;

    @JsonProperty("locality")
    private final String locality;

    @JsonProperty("po_box")
    private final String poBox;

    @JsonProperty("postal_code")
    private final String postalCode;

    @JsonProperty("premises")
    private final String premises;

    @JsonProperty("region")
    private final String region;

    @JsonProperty("care_of_name")
    private final String careOfName;

    private CompanySearchAddress(Builder builder) {
        addressLine1 = builder.addressLine1;
        addressLine2 = builder.addressLine2;
        careOf = builder.careOf;
        country = builder.country;
        locality = builder.locality;
        poBox = builder.poBox;
        postalCode = builder.postalCode;
        premises = builder.premises;
        region = builder.region;
        careOfName = builder.careOfName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCareOf() {
        return careOf;
    }

    public String getCountry() {
        return country;
    }

    public String getLocality() {
        return locality;
    }

    public String getPoBox() {
        return poBox;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPremises() {
        return premises;
    }

    public String getRegion() {
        return region;
    }

    public static final class Builder {

        private String addressLine1;

        private String addressLine2;

        private String careOf;

        private String country;

        private String locality;

        private String poBox;

        private String postalCode;

        private String premises;

        private String region;

        private String careOfName;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public CompanySearchAddress.Builder addressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
            return this;
        }

        public CompanySearchAddress.Builder addressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
            return this;
        }

        public CompanySearchAddress.Builder careOf(String careOf) {
            this.careOf = careOf;
            return this;
        }

        public CompanySearchAddress.Builder country(String country) {
            this.country = country;
            return this;
        }

        public CompanySearchAddress.Builder locality(String locality) {
            this.locality = locality;
            return this;
        }

        public CompanySearchAddress.Builder poBox(String poBox) {
            this.poBox = poBox;
            return this;
        }

        public CompanySearchAddress.Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public CompanySearchAddress.Builder premises(String premises) {
            this.premises = premises;
            return this;
        }

        public CompanySearchAddress.Builder region(String region) {
            this.region = region;
            return this;
        }

        public CompanySearchAddress.Builder careOfName(String careOfName) {
            this.careOfName = careOfName;
            return this;
        }

        public CompanySearchAddress build() {
            return new CompanySearchAddress(this);
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
        CompanySearchAddress that = (CompanySearchAddress) o;
        return Objects.equals(addressLine1, that.addressLine1) && Objects.equals(addressLine2,
                that.addressLine2) && Objects.equals(careOf, that.careOf) && Objects.equals(country,
                that.country) && Objects.equals(locality, that.locality) && Objects.equals(poBox,
                that.poBox) && Objects.equals(postalCode, that.postalCode) && Objects.equals(premises,
                that.premises) && Objects.equals(region, that.region) &&
                Objects.equals(careOfName, that.careOfName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressLine1, addressLine2, careOf, country, locality, poBox, postalCode,
                premises, region, careOfName);
    }
}
