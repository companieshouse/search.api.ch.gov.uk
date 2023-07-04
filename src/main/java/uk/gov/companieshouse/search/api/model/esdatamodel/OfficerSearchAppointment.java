package uk.gov.companieshouse.search.api.model.esdatamodel;

import java.time.LocalDate;

public class OfficerSearchAppointment {

    private static final String RECORD_TYPE = "officers";
    private AppointmentAddress address;
    private final LocalDate appointedBefore;
    private final LocalDate appointedOn;
    private final String corporateNameStart;
    private final String corporateNameEnding;
    private final String forename;
    private final String fullAddress;
    private final LocalDate lastResignedOn;
    private final String officerRole;
    private final String otherForenames;
    private final String personName;
    private final String personTitleName;
    private final LocalDate resignedOn;
    private final String surname;
    private final String title;
    private String wildcardKey;
    private final String recordType;

    private OfficerSearchAppointment(Builder builder) {
        address = builder.address;
        appointedBefore = builder.appointedBefore;
        appointedOn = builder.appointedOn;
        corporateNameStart = builder.corporateNameStart;
        corporateNameEnding = builder.corporateNameEnding;
        forename = builder.forename;
        fullAddress = builder.fullAddress;
        lastResignedOn = builder.lastResignedOn;
        officerRole = builder.officerRole;
        otherForenames = builder.otherForenames;
        personName = builder.personName;
        personTitleName = builder.personTitleName;
        resignedOn = builder.resignedOn;
        surname = builder.surname;
        title = builder.title;
        wildcardKey = builder.wildcardKey;
        recordType = builder.recordType;
    }

    public AppointmentAddress getAddress() {
        return address;
    }

    public OfficerSearchAppointment address(AppointmentAddress address) {
        this.address = address;
        return this;
    }

    public LocalDate getAppointedBefore() {
        return appointedBefore;
    }

    public LocalDate getAppointedOn() {
        return appointedOn;
    }

    public String getCorporateNameStart() {
        return corporateNameStart;
    }

    public String getCorporateNameEnding() {
        return corporateNameEnding;
    }

    public String getForename() {
        return forename;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public LocalDate getLastResignedOn() {
        return lastResignedOn;
    }

    public String getOfficerRole() {
        return officerRole;
    }

    public String getOtherForenames() {
        return otherForenames;
    }

    public String getPersonName() {
        return personName;
    }

    public String getPersonTitleName() {
        return personTitleName;
    }

    public LocalDate getResignedOn() {
        return resignedOn;
    }

    public String getSurname() {
        return surname;
    }

    public String getTitle() {
        return title;
    }

    public String getWildcardKey() {
        return wildcardKey;
    }

    public OfficerSearchAppointment wildcardKey(String wildcardKey) {
        this.wildcardKey = wildcardKey;
        return this;
    }

    public String getRecordType() {
        return recordType;
    }

    public static final class Builder {

        private AppointmentAddress address;
        private LocalDate appointedBefore;
        private LocalDate appointedOn;
        private String corporateNameStart;
        private String corporateNameEnding;
        private String forename;
        private String fullAddress;
        private LocalDate lastResignedOn;
        private String officerRole;
        private String otherForenames;
        private String personName;
        private String personTitleName;
        private LocalDate resignedOn;
        private String surname;
        private String title;
        private String wildcardKey;
        private final String recordType;

        private Builder() {
            this.recordType = RECORD_TYPE;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder address(AppointmentAddress address) {
            this.address = address;
            return this;
        }

        public Builder appointedBefore(LocalDate appointedBefore) {
            this.appointedBefore = appointedBefore;
            return this;
        }

        public Builder appointedOn(LocalDate appointedOn) {
            this.appointedOn = appointedOn;
            return this;
        }

        public Builder corporateNameStart(String corporateNameStart) {
            this.corporateNameStart = corporateNameStart;
            return this;
        }

        public Builder corporateNameEnding(String corporateNameEnding) {
            this.corporateNameEnding = corporateNameEnding;
            return this;
        }

        public Builder forename(String forename) {
            this.forename = forename;
            return this;
        }

        public Builder fullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
            return this;
        }

        public Builder lastResignedOn(LocalDate lastResignedOn) {
            this.lastResignedOn = lastResignedOn;
            return this;
        }

        public Builder officerRole(String officerRole) {
            this.officerRole = officerRole;
            return this;
        }

        public Builder otherForenames(String otherForenames) {
            this.otherForenames = otherForenames;
            return this;
        }

        public Builder personName(String personName) {
            this.personName = personName;
            return this;
        }

        public Builder personTitleName(String personTitleName) {
            this.personTitleName = personTitleName;
            return this;
        }

        public Builder resignedOn(LocalDate resignedOn) {
            this.resignedOn = resignedOn;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder wildcardKey(String wildcardKey) {
            this.wildcardKey = wildcardKey;
            return this;
        }

        public OfficerSearchAppointment build() {
            return new OfficerSearchAppointment(this);
        }
    }
}
