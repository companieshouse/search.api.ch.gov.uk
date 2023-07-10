package uk.gov.companieshouse.search.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
