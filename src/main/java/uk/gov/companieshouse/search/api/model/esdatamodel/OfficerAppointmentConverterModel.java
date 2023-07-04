package uk.gov.companieshouse.search.api.model.esdatamodel;

import java.time.LocalDate;
import java.util.Objects;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;

public class OfficerAppointmentConverterModel {

    private OfficerAppointmentSummary officerAppointmentSummary;
    private LocalDate lastResignedOn;
    private boolean isCorporateOfficer;

    public OfficerAppointmentSummary getOfficerAppointmentSummary() {
        return officerAppointmentSummary;
    }

    public OfficerAppointmentConverterModel officerAppointmentSummary(
            OfficerAppointmentSummary officerAppointmentSummary) {
        this.officerAppointmentSummary = officerAppointmentSummary;
        return this;
    }

    public LocalDate getLastResignedOn() {
        return lastResignedOn;
    }

    public OfficerAppointmentConverterModel lastResignedOn(LocalDate lastResignedOn) {
        this.lastResignedOn = lastResignedOn;
        return this;
    }

    public boolean isCorporateOfficer() {
        return isCorporateOfficer;
    }

    public OfficerAppointmentConverterModel corporateOfficer(boolean corporateOfficer) {
        isCorporateOfficer = corporateOfficer;
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
        OfficerAppointmentConverterModel that = (OfficerAppointmentConverterModel) o;
        return isCorporateOfficer == that.isCorporateOfficer && Objects.equals(officerAppointmentSummary,
                that.officerAppointmentSummary) && Objects.equals(lastResignedOn, that.lastResignedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(officerAppointmentSummary, lastResignedOn, isCorporateOfficer);
    }

    @Override
    public String toString() {
        return "OfficerAppointmentConverterModel{" +
                "officerAppointmentSummary=" + officerAppointmentSummary +
                ", lastResignedOn=" + lastResignedOn +
                ", isCorporateOfficer=" + isCorporateOfficer +
                '}';
    }
}
