package uk.gov.companieshouse.search.api.mapper;

import com.google.common.collect.Iterables;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.model.esdatamodel.AppointmentAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerAppointmentConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchLinks;
import uk.gov.companieshouse.search.api.util.AlphaKeyMapper;

@Component
public class OfficerAppointmentsListConverter implements Converter<AppointmentList, OfficerSearchDocument> {

    private static final String CLEAN_NAME_ELEMENTS_REGEX = "^[\\W_]+";
    private final ConversionService officerAppointmentSummaryConverter;
    private final ConversionService appointmentAddressConverter;
    private final AlphaKeyMapper alphaKeyMapper;

    public OfficerAppointmentsListConverter(@Lazy ConversionService officerAppointmentSummaryConverter,
            @Lazy ConversionService appointmentAddressConverter, AlphaKeyMapper alphaKeyMapper) {
        this.officerAppointmentSummaryConverter = officerAppointmentSummaryConverter;
        this.appointmentAddressConverter = appointmentAddressConverter;
        this.alphaKeyMapper = alphaKeyMapper;
    }

    @Override
    public OfficerSearchDocument convert(AppointmentList appointmentList) {
        String sortKey = alphaKeyMapper.makeSortKey(appointmentList);

        // appointment list will have the most recently resigned appointment, if one exists, at the end of the list
        LocalDate lastResignedOn = Iterables.getLast(appointmentList.getItems()).getResignedOn();

        // clean name elements
        appointmentList.getItems().forEach(officerAppointmentSummary ->
                Optional.ofNullable(officerAppointmentSummary.getNameElements())
                        .ifPresent(elements -> {
                            Optional.ofNullable(elements.getForename())
                                    .ifPresent(forename -> elements.setForename(
                                            forename.replaceAll(CLEAN_NAME_ELEMENTS_REGEX, "")));
                            Optional.ofNullable(elements.getOtherForenames())
                                    .ifPresent(otherForenames -> elements.setOtherForenames(
                                            otherForenames.replaceAll(CLEAN_NAME_ELEMENTS_REGEX, "")));
                            Optional.ofNullable(elements.getSurname())
                                    .ifPresent(surname -> elements.setSurname(
                                            surname.replaceAll(CLEAN_NAME_ELEMENTS_REGEX, "")));
                        }));

        OfficerSearchDocument document = OfficerSearchDocument.Builder.builder()
                .activeCount(appointmentList.getActiveCount())
                .inactiveCount(appointmentList.getInactiveCount())
                .dateOfBirth(appointmentList.getDateOfBirth())
                .items(appointmentList.getItems().stream()
                        .map(officerAppointmentSummary -> officerAppointmentSummaryConverter.convert(
                                new OfficerAppointmentConverterModel()
                                        .officerAppointmentSummary(officerAppointmentSummary)
                                        .lastResignedOn(lastResignedOn)
                                        .corporateOfficer(appointmentList.getIsCorporateOfficer()),
                                OfficerSearchAppointment.class))
                        .collect(Collectors.toList()))
                .links(new OfficerSearchLinks(appointmentList.getLinks().getSelf()))
                .resignedCount(appointmentList.getResignedCount())
                .sortKey(sortKey)
                .build();

        // set address and wildcard key on the first item in the list only
        appointmentList.getItems().stream()
                .findFirst()
                .ifPresent(officerAppointmentSummary -> document.getItems().get(0)
                        .address(appointmentAddressConverter.convert(officerAppointmentSummary.getAddress(),
                                AppointmentAddress.class))
                        .wildcardKey(sortKey));

        return document;
    }
}
