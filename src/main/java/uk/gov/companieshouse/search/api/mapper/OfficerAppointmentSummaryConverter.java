package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.util.AddressUtils.getFullAddressString;
import static uk.gov.companieshouse.search.api.util.OfficerNameUtils.getCorporateNameEndings;
import static uk.gov.companieshouse.search.api.util.OfficerNameUtils.getPersonTitle;

import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerAppointmentConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;

@Component
public class OfficerAppointmentSummaryConverter implements
        Converter<OfficerAppointmentConverterModel, OfficerSearchAppointment> {

    @Override
    public OfficerSearchAppointment convert(OfficerAppointmentConverterModel converterModel) {
        OfficerAppointmentSummary appointment = converterModel.getOfficerAppointmentSummary();

        OfficerSearchAppointment searchAppointment = new OfficerSearchAppointment()
                .officerRole(appointment.getOfficerRole().toString())
                .fullAddress(getFullAddressString(appointment.getAddress()))
                .appointedOn(appointment.getAppointedOn())
                .appointedBefore(appointment.getAppointedBefore())
                .resignedOn(appointment.getResignedOn());

        if (converterModel.isCorporateOfficer()) {
            Pair<String, String> corporateNameEndings = getCorporateNameEndings(appointment.getName());
            searchAppointment.corporateNameStart(corporateNameEndings.getLeft())
                    .corporateNameEnding(corporateNameEndings.getRight());
        } else {
            searchAppointment.personName(appointment.getName());
            Optional.ofNullable(appointment.getNameElements())
                    .ifPresent(nameElements -> searchAppointment.forename(nameElements.getForename())
                            .otherForenames(nameElements.getOtherForenames())
                            .surname(nameElements.getSurname())
                            .title(getPersonTitle(nameElements.getTitle())));
            searchAppointment.personTitleName(Optional.ofNullable(searchAppointment.getTitle())
                    .map(title -> title + " " + appointment.getName())
                    .orElse(appointment.getName()));
        }

        return searchAppointment;
    }
}
