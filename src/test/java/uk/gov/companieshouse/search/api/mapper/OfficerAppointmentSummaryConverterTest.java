package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerAppointmentConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;

class OfficerAppointmentSummaryConverterTest {

    private static final LocalDate DATE = LocalDate.of(2023, 7, 4);
    private OfficerAppointmentSummaryConverter converter;

    @BeforeEach
    void setUp() {
        converter = new OfficerAppointmentSummaryConverter();
    }

    @Test
    void convertNaturalOfficer() {
        // given
        OfficerAppointmentSummary appointmentSummary = new OfficerAppointmentSummary();

        OfficerAppointmentConverterModel converterModel = new OfficerAppointmentConverterModel()
                .officerAppointmentSummary(appointmentSummary)
                .lastResignedOn(DATE);

        OfficerSearchAppointment expected = OfficerSearchAppointment.Builder.builder().build();

        // when
        OfficerSearchAppointment actual = converter.convert(converterModel);

        // then
        assertEquals(expected, actual);
    }
}