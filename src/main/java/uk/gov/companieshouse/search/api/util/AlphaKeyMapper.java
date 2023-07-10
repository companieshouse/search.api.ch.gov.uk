package uk.gov.companieshouse.search.api.util;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;

@Component
public class AlphaKeyMapper {

    public String makeSortKey(AppointmentList appointmentList) {
        if (appointmentList.getIsCorporateOfficer()) {
            return "2"; // there is a bug in chs-backend where corporate officer sort keys are set to just 2 with
            // no name. This is replicated here until the business give the go ahead to use the alpha-key-service.
        } else {
            OfficerAppointmentSummary firstAppointment = appointmentList.getItems().get(0);
            return String.format("%s %s %s%d",
                    firstAppointment.getNameElements().getSurname(),
                    firstAppointment.getNameElements().getForename(),
                    firstAppointment.getNameElements().getOtherForenames(),
                    2);
        }
    }
}
