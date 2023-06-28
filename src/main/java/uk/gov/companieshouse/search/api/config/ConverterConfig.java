package uk.gov.companieshouse.search.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.search.api.mapper.AppointmentAddressConverter;
import uk.gov.companieshouse.search.api.mapper.OfficerAppointmentSummaryConverter;
import uk.gov.companieshouse.search.api.mapper.OfficerAppointmentsListConverter;

@Configuration
public class ConverterConfig implements WebMvcConfigurer {

    private final OfficerAppointmentSummaryConverter officerAppointmentSummaryConverter;
    private final AppointmentAddressConverter appointmentAddressConverter;
    private final OfficerAppointmentsListConverter officerAppointmentsListConverter;

    public ConverterConfig(OfficerAppointmentSummaryConverter officerAppointmentSummaryConverter,
            AppointmentAddressConverter appointmentAddressConverter,
            OfficerAppointmentsListConverter officerAppointmentsListConverter) {
        this.officerAppointmentSummaryConverter = officerAppointmentSummaryConverter;
        this.appointmentAddressConverter = appointmentAddressConverter;
        this.officerAppointmentsListConverter = officerAppointmentsListConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(officerAppointmentsListConverter);
        registry.addConverter(officerAppointmentSummaryConverter);
        registry.addConverter(appointmentAddressConverter);
    }
}
