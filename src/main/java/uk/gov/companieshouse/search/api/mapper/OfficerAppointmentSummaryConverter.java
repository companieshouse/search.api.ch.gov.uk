package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.util.CorporateOfficerNameUtils.getCorporateNameEndings;
import static uk.gov.companieshouse.search.api.util.SortWildCardKeyUtils.makeWildcardKey;

import java.time.LocalDate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.search.api.model.esdatamodel.AppointmentAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment.Builder;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

@Component
public class OfficerAppointmentSummaryConverter implements
        Converter<OfficerAppointmentSummary, OfficerSearchAppointment> {

    private final ConversionService conversionService;

    private final AlphaKeyService alphaKeyService;

    public OfficerAppointmentSummaryConverter(@Lazy ConversionService conversionService,
            AlphaKeyService alphaKeyService) {
        this.conversionService = conversionService;
        this.alphaKeyService = alphaKeyService;
    }

    @Override
    public OfficerSearchAppointment convert(OfficerAppointmentSummary appointment) {
        Pair<String, String> corporateNameEndings = getCorporateNameEndings(appointment.getName());
        return Builder.builder()
                .appointedOn(appointment.getAppointedOn())
                .appointedBefore(appointment.getAppointedBefore())
                .address(conversionService.convert(appointment.getAddress(), AppointmentAddress.class))
                .corporateNameStart(corporateNameEndings.getLeft())
                .corporateNameEnding(corporateNameEndings.getRight())
                .forename(appointment.getNameElements().getForename())
                .fullAddress(String.format(appointment.getAddress().getAddressLine1(),
                        appointment.getAddress().getAddressLine2(),
                        appointment.getAddress().getCountry(),
                        appointment.getAddress().getPostalCode()))
                .lastResignedOn(getLastResignedOn(appointment))
                .officerRole(String.valueOf(appointment.getOfficerRole()))
                .otherForenames(appointment.getNameElements().getOtherForenames())
                .personName(appointment.getName())
                .personTitleName(String.format(appointment.getNameElements().getTitle(), appointment.getName()))
                .resignedOn(appointment.getResignedOn())
                .surname(appointment.getNameElements().getSurname())
                .title(appointment.getNameElements().getTitle())
                .wildcardKey(makeWildcardKey(appointment))
                .build();
    }

    private LocalDate getLastResignedOn(OfficerAppointmentSummary appointment) {
        LocalDate lastResignedOn = null;
            if(StringUtils.isNotEmpty(String.valueOf(appointment.getResignedOn()))
                    && (appointment.getResignedOn().isAfter(lastResignedOn))) {
                lastResignedOn = appointment.getResignedOn();
            }
        return lastResignedOn;
    }

}
