package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.util.SortWildCardKeyUtils.makeSortKey;

import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument.Builder;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchLinks;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

@Component
public class OfficerAppointmentsListConverter implements
        Converter<AppointmentList, OfficerSearchDocument> {

    private static final String RESOURCE_KIND ="searchresults#officer";
    private final ConversionService conversionService;

    public OfficerAppointmentsListConverter(@Lazy ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public OfficerSearchDocument convert(AppointmentList appointmentList) {
        return Builder.builder()
                .activeCount(appointmentList.getActiveCount())
                .inactiveCount(appointmentList.getInactiveCount())
                .dateOfBirth(appointmentList.getDateOfBirth())
                .items(appointmentList.getItems().stream()
                        .map(officerAppointmentSummary -> conversionService
                                .convert(officerAppointmentSummary, OfficerSearchAppointment.class))
                        .collect(Collectors.toList()))
                .kind(RESOURCE_KIND)
                .links(new OfficerSearchLinks(appointmentList.getLinks().getSelf()))
                .resignedCount(appointmentList.getResignedCount())
                .sortKey(makeSortKey(appointmentList))
                .build();
    }
}
