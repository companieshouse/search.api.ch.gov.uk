package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.util.AddressUtils.getFullAddressString;
import static uk.gov.companieshouse.search.api.util.CorporateOfficerNameUtils.getCorporateNameEndings;

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
        boolean isCorporateOfficer = converterModel.isCorporateOfficer();
        Pair<String, String> corporateNameEndings = getCorporateNameEndings(appointment.getName());

        OfficerSearchAppointment searchAppointment = OfficerSearchAppointment.Builder.builder()
                .officerRole(appointment.getOfficerRole().toString())
                .fullAddress(getFullAddressString(appointment.getAddress()))
                .appointedOn(appointment.getAppointedOn())
                .appointedBefore(appointment.getAppointedBefore())
                .resignedOn(appointment.getResignedOn())
                .build();

        if (converterModel.isCorporateOfficer()) {
            searchAppointment.nameEn
        } else {

        }

        return searchAppointment;

        return OfficerSearchAppointment.Builder.builder()
                .appointedOn(appointment.getAppointedOn())
                .appointedBefore(appointment.getAppointedBefore())
                .corporateNameStart(corporateNameEndings.getLeft())
                .corporateNameEnding(corporateNameEndings.getRight())
                .forename(appointment.getNameElements().getForename())
                .fullAddress(getFullAddressString(converterModel.getOfficerAppointmentSummary().getAddress()))
                .lastResignedOn(converterModel.getLastResignedOn())
                .officerRole(String.valueOf(appointment.getOfficerRole()))
                .otherForenames(appointment.getNameElements().getOtherForenames())
                .personName(appointment.getName())
                .personTitleName(String.format(appointment.getNameElements().getTitle(), appointment.getName()))
                .resignedOn(appointment.getResignedOn())
                .surname(appointment.getNameElements().getSurname())
                .title(appointment.getNameElements().getTitle())
                .build();
    }
}
