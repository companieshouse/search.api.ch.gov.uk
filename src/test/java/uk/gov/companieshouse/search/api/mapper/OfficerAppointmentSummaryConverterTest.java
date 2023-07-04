package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.companieshouse.api.officer.OfficerAppointmentSummary.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.officer.Address;
import uk.gov.companieshouse.api.officer.NameElements;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerAppointmentConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;
import uk.gov.companieshouse.search.api.util.AddressUtils;

class OfficerAppointmentSummaryConverterTest {

    private static final LocalDate DATE = LocalDate.of(2023, 7, 4);
    private static final String FULL_ADDRESS = "Care Of, PO BOX, 123 Address Line 1, Address Line 2, Locality, Region, Country, Postal Code";
    private static final String RECORD_TYPE = "officers";
    private OfficerAppointmentSummaryConverter converter;

    @BeforeEach
    void setUp() {
        converter = new OfficerAppointmentSummaryConverter();
    }

    @Test
    void convertNaturalOfficer() {
        // given
        OfficerAppointmentSummary appointmentSummary = new OfficerAppointmentSummary()
                .appointedOn(DATE)
                .appointedBefore(DATE)
                .resignedOn(DATE)
                .officerRole(OfficerRoleEnum.DIRECTOR)
                .address(new Address()
                        .careOf("Care Of")
                        .poBox("PO BOX")
                        .premises("123")
                        .addressLine1("Address Line 1")
                        .addressLine2("Address Line 2")
                        .locality("Locality")
                        .region("Region")
                        .country("Country")
                        .postalCode("Postal Code"))
                .nameElements(new NameElements()
                        .forename("Forename")
                        .otherForenames("Other Forenames")
                        .surname("Surname")
                        .title("Dr"))
                .name("Forename Other Forenames Surname");

        OfficerAppointmentConverterModel converterModel = new OfficerAppointmentConverterModel()
                .officerAppointmentSummary(appointmentSummary)
                .lastResignedOn(DATE);

        OfficerSearchAppointment expected =
                OfficerSearchAppointment.Builder.builder()
                        .officerRole(OfficerRoleEnum.DIRECTOR.toString())
                        .fullAddress(FULL_ADDRESS)
                        .forename("Forename")
                        .otherForenames("Other Forenames")
                        .surname("Surname")
                        .personName("Forename Other Forenames Surname")
                        .title("Dr")
                        .personTitleName("Dr Forename Other Forenames Surname")
                        .appointedOn(DATE)
                        .appointedBefore(DATE)
                        .resignedOn(DATE)
                        .recordType(RECORD_TYPE)
                        .build();

        // when
        OfficerSearchAppointment actual = converter.convert(converterModel);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void convertCorporateOfficer() {
        // given
        OfficerAppointmentSummary appointmentSummary = new OfficerAppointmentSummary()
                .appointedOn(DATE)
                .appointedBefore(DATE)
                .resignedOn(DATE)
                .officerRole(OfficerRoleEnum.CORPORATE_DIRECTOR)
                .address(new Address()
                        .careOf("Care Of")
                        .poBox("PO BOX")
                        .premises("123")
                        .addressLine1("Address Line 1")
                        .addressLine2("Address Line 2")
                        .locality("Locality")
                        .region("Region")
                        .country("Country")
                        .postalCode("Postal Code"))
                .name("Corporate Company Ltd");

        OfficerAppointmentConverterModel converterModel = new OfficerAppointmentConverterModel()
                .officerAppointmentSummary(appointmentSummary)
                .lastResignedOn(DATE);

        OfficerSearchAppointment expected =
                OfficerSearchAppointment.Builder.builder()
                        .officerRole(OfficerRoleEnum.CORPORATE_DIRECTOR.toString())
                        .corporateNameStart("Corporate Company")
                        .corporateNameEnding("Ltd")
                        .fullAddress(FULL_ADDRESS)
                        .appointedOn(DATE)
                        .appointedBefore(DATE)
                        .resignedOn(DATE)
                        .recordType(RECORD_TYPE)
                        .build();

        // when
        OfficerSearchAppointment actual = converter.convert(converterModel);

        // then
        assertEquals(expected, actual);
    }
}