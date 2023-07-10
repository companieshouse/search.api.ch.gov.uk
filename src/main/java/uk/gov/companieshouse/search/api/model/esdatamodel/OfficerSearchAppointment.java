package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Objects;

public class OfficerSearchAppointment {

    private static final String RECORD_TYPE = "officers";
    @JsonProperty("address")
    private AppointmentAddress address;
    @JsonProperty("appointed_before")
    private LocalDate appointedBefore;
    @JsonProperty("appointed_on")
    private LocalDate appointedOn;
    @JsonProperty("corporate_name_start")
    private String corporateNameStart;
    @JsonProperty("corporate_name_ending")
    private String corporateNameEnding;
    @JsonProperty("forename")
    private String forename;
    @JsonProperty("full_address")
    private String fullAddress;
    @JsonProperty("last_resigned_on")
    private LocalDate lastResignedOn;
    @JsonProperty("officer_role")
    private String officerRole;
    @JsonProperty("other_forenames")
    private String otherForenames;
    @JsonProperty("person_name")
    private String personName;
    @JsonProperty("person_title_name")
    private String personTitleName;
    @JsonProperty("resigned_on")
    private LocalDate resignedOn;
    @JsonProperty("surname")
    private String surname;
    @JsonProperty("title")
    private String title;
    @JsonProperty("wildcard_key")
    private String wildcardKey;
    @JsonProperty("record_type")
    private final String recordType;

    public OfficerSearchAppointment() {
        this.recordType = RECORD_TYPE;
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

    public OfficerSearchAppointment appointedBefore(LocalDate appointedBefore) {
        this.appointedBefore = appointedBefore;
        return this;
    }

    public LocalDate getAppointedOn() {
        return appointedOn;
    }

    public OfficerSearchAppointment appointedOn(LocalDate appointedOn) {
        this.appointedOn = appointedOn;
        return this;
    }

    public String getCorporateNameStart() {
        return corporateNameStart;
    }

    public OfficerSearchAppointment corporateNameStart(String corporateNameStart) {
        this.corporateNameStart = corporateNameStart;
        return this;
    }

    public String getCorporateNameEnding() {
        return corporateNameEnding;
    }

    public OfficerSearchAppointment corporateNameEnding(String corporateNameEnding) {
        this.corporateNameEnding = corporateNameEnding;
        return this;
    }

    public String getForename() {
        return forename;
    }

    public OfficerSearchAppointment forename(String forename) {
        this.forename = forename;
        return this;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public OfficerSearchAppointment fullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
        return this;
    }

    public LocalDate getLastResignedOn() {
        return lastResignedOn;
    }

    public OfficerSearchAppointment lastResignedOn(LocalDate lastResignedOn) {
        this.lastResignedOn = lastResignedOn;
        return this;
    }

    public String getOfficerRole() {
        return officerRole;
    }

    public OfficerSearchAppointment officerRole(String officerRole) {
        this.officerRole = officerRole;
        return this;
    }

    public String getOtherForenames() {
        return otherForenames;
    }

    public OfficerSearchAppointment otherForenames(String otherForenames) {
        this.otherForenames = otherForenames;
        return this;
    }

    public String getPersonName() {
        return personName;
    }

    public OfficerSearchAppointment personName(String personName) {
        this.personName = personName;
        return this;
    }

    public String getPersonTitleName() {
        return personTitleName;
    }

    public OfficerSearchAppointment personTitleName(String personTitleName) {
        this.personTitleName = personTitleName;
        return this;
    }

    public LocalDate getResignedOn() {
        return resignedOn;
    }

    public OfficerSearchAppointment resignedOn(LocalDate resignedOn) {
        this.resignedOn = resignedOn;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public OfficerSearchAppointment surname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public OfficerSearchAppointment title(String title) {
        this.title = title;
        return this;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OfficerSearchAppointment that = (OfficerSearchAppointment) o;
        return Objects.equals(address, that.address) && Objects.equals(appointedBefore,
                that.appointedBefore) && Objects.equals(appointedOn, that.appointedOn)
                && Objects.equals(corporateNameStart, that.corporateNameStart) && Objects.equals(
                corporateNameEnding, that.corporateNameEnding) && Objects.equals(forename, that.forename)
                && Objects.equals(fullAddress, that.fullAddress) && Objects.equals(lastResignedOn,
                that.lastResignedOn) && Objects.equals(officerRole, that.officerRole) && Objects.equals(
                otherForenames, that.otherForenames) && Objects.equals(personName, that.personName)
                && Objects.equals(personTitleName, that.personTitleName) && Objects.equals(resignedOn,
                that.resignedOn) && Objects.equals(surname, that.surname) && Objects.equals(title,
                that.title) && Objects.equals(wildcardKey, that.wildcardKey) && Objects.equals(
                recordType, that.recordType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, appointedBefore, appointedOn, corporateNameStart, corporateNameEnding, forename,
                fullAddress, lastResignedOn, officerRole, otherForenames, personName, personTitleName, resignedOn,
                surname,
                title, wildcardKey, recordType);
    }

    @Override
    public String toString() {
        return "OfficerSearchAppointment{" +
                "address=" + address +
                ", appointedBefore=" + appointedBefore +
                ", appointedOn=" + appointedOn +
                ", corporateNameStart='" + corporateNameStart + '\'' +
                ", corporateNameEnding='" + corporateNameEnding + '\'' +
                ", forename='" + forename + '\'' +
                ", fullAddress='" + fullAddress + '\'' +
                ", lastResignedOn=" + lastResignedOn +
                ", officerRole='" + officerRole + '\'' +
                ", otherForenames='" + otherForenames + '\'' +
                ", personName='" + personName + '\'' +
                ", personTitleName='" + personTitleName + '\'' +
                ", resignedOn=" + resignedOn +
                ", surname='" + surname + '\'' +
                ", title='" + title + '\'' +
                ", wildcardKey='" + wildcardKey + '\'' +
                ", recordType='" + recordType + '\'' +
                '}';
    }
}
