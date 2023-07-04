package uk.gov.companieshouse.search.api.util;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

@Component
public class AlphaKeyMapper {

    private final AlphaKeyService alphaKeyService;

    public AlphaKeyMapper(AlphaKeyService alphaKeyService) {
        this.alphaKeyService = alphaKeyService;
    }

    public String makeSortKey(AppointmentList appointmentList) {
        if (appointmentList.getIsCorporateOfficer()) {
            AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(appointmentList.getItems().get(0).getName());
            if (alphaKeyResponse != null) {
                return String.format("%s%d", alphaKeyResponse.getOrderedAlphaKey(), 2);
            }
        } else {
            OfficerAppointmentSummary firstAppointment = appointmentList.getItems().get(0);
            return String.format("%s %s %s%d",
                    firstAppointment.getNameElements().getSurname(),
                    firstAppointment.getNameElements().getForename(),
                    firstAppointment.getNameElements().getOtherForenames(),
                    2);
        }
        return "2";
    }
}
