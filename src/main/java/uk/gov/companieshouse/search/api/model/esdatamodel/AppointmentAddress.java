package uk.gov.companieshouse.search.api.model.esdatamodel;

public class AppointmentAddress {

    private final String addressLine1;
    private final String addressLine2;
    private final String careOf;
    private final String country;
    private final String locality;
    private final String poBox;
    private final String postalCode;
    private final String premises;
    private final String region;

    public AppointmentAddress(Builder builder) {
        addressLine1 = builder.addressLine1;
        addressLine2 = builder.addressLine2;
        careOf = builder.careOf;
        country = builder.country;
        locality = builder.locality;
        poBox = builder.poBox;
        postalCode = builder.postalCode;
        premises = builder.premises;
        region = builder.region;
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

        private final String addressLine1;
        private final String addressLine2;
        private final String careOf;
        private final String country;
        private final String locality;
        private final String poBox;
        private final String postalCode;
        private final String premises;
        private final String region;

        private Builder(String addressLine1, String addressLine2, String careOf, String country, String locality,
                String poBox, String postalCode, String premises, String region) {
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
            this.careOf = careOf;
            this.country = country;
            this.locality = locality;
            this.poBox = poBox;
            this.postalCode = postalCode;
            this.premises = premises;
            this.region = region;
        }

        public static Builder builder(String addressLine1, String addressLine2, String careOf, String country,
                String locality, String poBox, String postalCode, String premises, String region) {
            return new Builder(addressLine1, addressLine2, careOf, country, locality, poBox, postalCode, premises,
                    region);
        }

        public AppointmentAddress build() {
            return new AppointmentAddress(this);
        }
    }
}
