package uk.gov.companieshouse.search.api.model.psc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PscSummary {
    @JsonProperty("address")
    public Address address;
    @JsonProperty("country_of_residence")
    public String countryOfResidence;
    @JsonProperty("date_of_birth")
    public DateOfBirth dateOfBirth;
    @JsonProperty("etag")
    public String etag;
    @JsonProperty("identity_verification_details")
    public IdentityVerificationDetails identityVerificationDetails;
    @JsonProperty("kind")
    public String kind;
    @JsonProperty("links")
    public Links links;
    @JsonProperty("name")
    public String name;
    @JsonProperty("name_elements")
    public NameElements nameElements;
    @JsonProperty("nationality")
    public String nationality;
    @JsonProperty("natures_of_control")
    public List<String> naturesOfControl;
    @JsonProperty("notified_on")
    public String notifiedOn;
    @JsonProperty("cessated_on")
    public String cessatedOn;
    // Add any other fields needed

    public static class Address {
        @JsonProperty("address_line_1")
        public String addressLine1;
        @JsonProperty("address_line_2")
        public String addressLine2;
        @JsonProperty("country")
        public String country;
        @JsonProperty("locality")
        public String locality;
        @JsonProperty("postal_code")
        public String postalCode;
        @JsonProperty("premises")
        public String premises;
        @JsonProperty("region")
        public String region;
    }

    public static class DateOfBirth {
        @JsonProperty("month")
        public Integer month;
        @JsonProperty("year")
        public Integer year;
    }

    public static class IdentityVerificationDetails {
        @JsonProperty("appointment_verification_end_on")
        public String appointmentVerificationEndOn;
        @JsonProperty("appointment_verification_start_on")
        public String appointmentVerificationStartOn;
    }

    public static class Links {
        @JsonProperty("self")
        public String self;
    }

    public static class NameElements {
        @JsonProperty("forename")
        public String forename;
        @JsonProperty("middle_name")
        public String middleName;
        @JsonProperty("surname")
        public String surname;
        @JsonProperty("title")
        public String title;
    }
}
