package uk.gov.companieshouse.search.api.util;

import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

public class SortWildCardKeyUtils {

    private static AlphaKeyService alphaKeyService;

    private SortWildCardKeyUtils() {
    }

    public static String makeSortKey(AppointmentList appointmentList) {
        if (appointmentList.getIsCorporateOfficer()) {
            AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForOfficer(
                    appointmentList.getItems().get(0).getName());
            if (alphaKeyResponse != null) {
                return String.format(alphaKeyResponse.getOrderedAlphaKey(), "2");
            }
        } else {
            OfficerAppointmentSummary firstAppointment = appointmentList.getItems().get(0);
            return String.format(
                    firstAppointment.getNameElements().getSurname(),
                    firstAppointment.getNameElements().getForename(),
                    firstAppointment.getNameElements().getOtherForenames(),
                    "2");
        }
        return "2";
    }

    public static String makeWildcardKey(OfficerAppointmentSummary appointment) {
        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForOfficer(appointment.getName());
        if (alphaKeyResponse != null) {
            return String.format(alphaKeyResponse.getOrderedAlphaKey(), "2");
        }
        return "2";
    }

}
