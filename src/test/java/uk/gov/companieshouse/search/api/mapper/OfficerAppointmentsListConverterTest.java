package uk.gov.companieshouse.search.api.mapper;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import uk.gov.companieshouse.api.officer.Address;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.api.officer.DateOfBirth;
import uk.gov.companieshouse.api.officer.NameElements;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.api.officer.OfficerLinkTypes;
import uk.gov.companieshouse.search.api.model.esdatamodel.AppointmentAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerAppointmentConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument.Builder;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchLinks;
import uk.gov.companieshouse.search.api.util.AlphaKeyMapper;

@ExtendWith(MockitoExtension.class)
class OfficerAppointmentsListConverterTest {

    private static final LocalDate DATE = LocalDate.of(2023, 7, 4);

    private OfficerAppointmentsListConverter converter;
    @Mock
    private ConversionService officerAppointmentSummaryConverter;
    @Mock
    private ConversionService appointmentAddressConverter;
    @Mock
    private AlphaKeyMapper alphaKeyMapper;

    @BeforeEach
    void setUp() {
        converter = new OfficerAppointmentsListConverter(officerAppointmentSummaryConverter,
                appointmentAddressConverter, alphaKeyMapper);
    }

    @Test
    @DisplayName("Should successfully convert officers appointment list to officers search document")
    void convert() {
        // given

        OfficerAppointmentSummary officerAppointmentSummary = new OfficerAppointmentSummary()
                .address(new Address())
                .resignedOn(DATE)
                .nameElements(new NameElements()
                        .forename("-forename")
                        .surname(" surname")
                        .otherForenames("_john"));

        AppointmentList appointmentList = new AppointmentList()
                .isCorporateOfficer(false)
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .name("natural officer")
                .dateOfBirth(new DateOfBirth()
                        .month(7)
                        .year(1990))
                .links(new OfficerLinkTypes().self("/officers/officerId/appointments"))
                .items(List.of(officerAppointmentSummary,
                        officerAppointmentSummary,
                        officerAppointmentSummary));

        OfficerAppointmentSummary expectedAppointmentSummary = new OfficerAppointmentSummary()
                .address(new Address())
                .resignedOn(DATE)
                .nameElements(new NameElements()
                        .forename("forename")
                        .surname("surname")
                        .otherForenames("john"));

        OfficerSearchAppointment appointment = new OfficerSearchAppointment();

        OfficerSearchDocument expected = Builder.builder()
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .dateOfBirth(new DateOfBirth()
                        .month(7)
                        .year(1990))
                .links(new OfficerSearchLinks("/officers/officerId/appointments"))
                .items(List.of(appointment, appointment, appointment))
                .sortKey("natural officer2")
                .build();

        when(alphaKeyMapper.makeSortKey(any())).thenReturn("natural officer2");
        when(officerAppointmentSummaryConverter.convert(any(), eq(OfficerSearchAppointment.class))).thenReturn(
                appointment);
        when(appointmentAddressConverter.convert(any(), eq(AppointmentAddress.class))).thenReturn(
                AppointmentAddress.Builder.builder().build());

        // when
        OfficerSearchDocument actual = converter.convert(appointmentList);

        // then
        assertEquals(expected, actual);
        verify(alphaKeyMapper).makeSortKey(appointmentList);
        verify(officerAppointmentSummaryConverter, times(3)).convert(new OfficerAppointmentConverterModel()
                        .officerAppointmentSummary(expectedAppointmentSummary)
                        .lastResignedOn(DATE),
                OfficerSearchAppointment.class);
        verify(appointmentAddressConverter).convert(expectedAppointmentSummary.getAddress(), AppointmentAddress.class);
    }

    @Test
    @DisplayName("Should successfully convert officers appointment list for false positive corporate officer to officers search document")
    void convertFalsePositiveCorporateOfficer() {
        // given

        // this test ensures a bug copied over from chs-backend still passes
        // e.g. when officer surname is one word (DOES NOT contain whitespace) it should be considered a
        // corporate officer
        // if the bug is to be fixed then a surname which DOES contain whitespace should indicate the officer is
        // a corporate one - after all other checks have been performed regarding dob, forename and other forenames
        OfficerAppointmentSummary officerAppointmentSummary = new OfficerAppointmentSummary()
                .address(new Address())
                .nameElements(new NameElements()
                        .surname("CORPORATE"));

        AppointmentList appointmentList = new AppointmentList()
                .isCorporateOfficer(false)
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .name("corporate officer")
                .links(new OfficerLinkTypes().self("/officers/officerId/appointments"))
                .items(List.of(officerAppointmentSummary,
                        officerAppointmentSummary,
                        officerAppointmentSummary.resignedOn(DATE)));

        OfficerSearchAppointment appointment = new OfficerSearchAppointment();

        OfficerSearchDocument expected = Builder.builder()
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .links(new OfficerSearchLinks("/officers/officerId/appointments"))
                .items(List.of(appointment, appointment, appointment))
                .sortKey("corporate officer2")
                .build();

        when(alphaKeyMapper.makeSortKey(any())).thenReturn("corporate officer2");
        when(officerAppointmentSummaryConverter.convert(any(), eq(OfficerSearchAppointment.class))).thenReturn(
                appointment);
        when(appointmentAddressConverter.convert(any(), eq(AppointmentAddress.class))).thenReturn(
                AppointmentAddress.Builder.builder().build());

        // when
        OfficerSearchDocument actual = converter.convert(appointmentList);

        // then
        assertEquals(expected, actual);
        verify(alphaKeyMapper).makeSortKey(appointmentList);
        verify(officerAppointmentSummaryConverter, times(3)).convert(new OfficerAppointmentConverterModel()
                        .officerAppointmentSummary(officerAppointmentSummary)
                        .lastResignedOn(DATE)
                        .corporateOfficer(true),
                OfficerSearchAppointment.class);
        verify(appointmentAddressConverter).convert(officerAppointmentSummary.getAddress(), AppointmentAddress.class);
    }

    @Test
    @DisplayName("Should successfully convert officers appointment list for corporate officer incorrectly considered to be natural to officers search document")
    void convertFalseNegativeCorporateOfficer() {
        // given

        // this test ensures a bug copied over from chs-backend still passes
        // e.g. when officer surname is multiple words (DOES contain whitespace) it should be considered a
        // natural officer
        // if the bug is to be fixed then a surname which DOES contain whitespace should indicate the officer is
        // a corporate one - after all other checks have been performed regarding dob, forename and other forenames
        OfficerAppointmentSummary officerAppointmentSummary = new OfficerAppointmentSummary()
                .address(new Address())
                .nameElements(new NameElements()
                        .surname("CORPORATE OFFICER CONSIDERED NATURAL LTD"));

        AppointmentList appointmentList = new AppointmentList()
                .isCorporateOfficer(false)
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .name("natural officer")
                .links(new OfficerLinkTypes().self("/officers/officerId/appointments"))
                .items(List.of(officerAppointmentSummary,
                        officerAppointmentSummary,
                        officerAppointmentSummary.resignedOn(DATE)));

        OfficerSearchAppointment appointment = new OfficerSearchAppointment();

        OfficerSearchDocument expected = Builder.builder()
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .links(new OfficerSearchLinks("/officers/officerId/appointments"))
                .items(List.of(appointment, appointment, appointment))
                .sortKey("natural officer2")
                .build();

        when(alphaKeyMapper.makeSortKey(any())).thenReturn("natural officer2");
        when(officerAppointmentSummaryConverter.convert(any(), eq(OfficerSearchAppointment.class))).thenReturn(
                appointment);
        when(appointmentAddressConverter.convert(any(), eq(AppointmentAddress.class))).thenReturn(
                AppointmentAddress.Builder.builder().build());

        // when
        OfficerSearchDocument actual = converter.convert(appointmentList);

        // then
        assertEquals(expected, actual);
        verify(alphaKeyMapper).makeSortKey(appointmentList);
        verify(officerAppointmentSummaryConverter, times(3)).convert(new OfficerAppointmentConverterModel()
                        .officerAppointmentSummary(officerAppointmentSummary)
                        .lastResignedOn(DATE)
                        .corporateOfficer(false),
                OfficerSearchAppointment.class);
        verify(appointmentAddressConverter).convert(officerAppointmentSummary.getAddress(), AppointmentAddress.class);
    }

    @Test
    @DisplayName("Should successfully convert officers appointment list for corporate officer to officers search document")
    void convertCorporateOfficer() {
        // given

        OfficerAppointmentSummary officerAppointmentSummary = new OfficerAppointmentSummary()
                .address(new Address());

        AppointmentList appointmentList = new AppointmentList()
                .isCorporateOfficer(true)
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .name("corporate officer")
                .dateOfBirth(new DateOfBirth()
                        .month(7)
                        .year(1990))
                .links(new OfficerLinkTypes().self("/officers/officerId/appointments"))
                .items(List.of(officerAppointmentSummary,
                        officerAppointmentSummary,
                        officerAppointmentSummary.resignedOn(DATE)));

        OfficerSearchAppointment appointment = new OfficerSearchAppointment();

        OfficerSearchDocument expected = Builder.builder()
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .dateOfBirth(new DateOfBirth()
                        .month(7)
                        .year(1990))
                .links(new OfficerSearchLinks("/officers/officerId/appointments"))
                .items(List.of(appointment, appointment, appointment))
                .sortKey("corporate officer2")
                .build();

        when(alphaKeyMapper.makeSortKey(any())).thenReturn("corporate officer2");
        when(officerAppointmentSummaryConverter.convert(any(), eq(OfficerSearchAppointment.class))).thenReturn(
                appointment);
        when(appointmentAddressConverter.convert(any(), eq(AppointmentAddress.class))).thenReturn(
                AppointmentAddress.Builder.builder().build());

        // when
        OfficerSearchDocument actual = converter.convert(appointmentList);

        // then
        assertEquals(expected, actual);
        verify(alphaKeyMapper).makeSortKey(appointmentList);
        verify(officerAppointmentSummaryConverter, times(3)).convert(new OfficerAppointmentConverterModel()
                        .officerAppointmentSummary(officerAppointmentSummary)
                        .lastResignedOn(DATE)
                        .corporateOfficer(true),
                OfficerSearchAppointment.class);
        verify(appointmentAddressConverter).convert(officerAppointmentSummary.getAddress(), AppointmentAddress.class);
    }

    @Test
    @DisplayName("Should successfully convert officers appointment list with no resigned appointments to officers search document")
    void convertNoResignedOn() {
        // given

        OfficerAppointmentSummary officerAppointmentSummary = new OfficerAppointmentSummary()
                .address(new Address());

        AppointmentList appointmentList = new AppointmentList()
                .isCorporateOfficer(false)
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .name("natural officer")
                .dateOfBirth(new DateOfBirth()
                        .month(7)
                        .year(1990))
                .links(new OfficerLinkTypes().self("/officers/officerId/appointments"))
                .items(List.of(officerAppointmentSummary,
                        officerAppointmentSummary,
                        officerAppointmentSummary));

        OfficerSearchAppointment appointment = new OfficerSearchAppointment();

        OfficerSearchDocument expected = Builder.builder()
                .activeCount(1)
                .inactiveCount(0)
                .resignedCount(2)
                .dateOfBirth(new DateOfBirth()
                        .month(7)
                        .year(1990))
                .links(new OfficerSearchLinks("/officers/officerId/appointments"))
                .items(List.of(appointment, appointment, appointment))
                .sortKey("natural officer2")
                .build();

        when(alphaKeyMapper.makeSortKey(any())).thenReturn("natural officer2");
        when(officerAppointmentSummaryConverter.convert(any(), eq(OfficerSearchAppointment.class))).thenReturn(
                appointment);
        when(appointmentAddressConverter.convert(any(), eq(AppointmentAddress.class))).thenReturn(
                AppointmentAddress.Builder.builder().build());

        // when
        OfficerSearchDocument actual = converter.convert(appointmentList);

        // then
        assertEquals(expected, actual);
        verify(alphaKeyMapper).makeSortKey(appointmentList);
        verify(officerAppointmentSummaryConverter, times(3)).convert(new OfficerAppointmentConverterModel()
                        .officerAppointmentSummary(officerAppointmentSummary)
                        .lastResignedOn(null),
                OfficerSearchAppointment.class);
        verify(appointmentAddressConverter).convert(officerAppointmentSummary.getAddress(), AppointmentAddress.class);
    }

    @Test
    @DisplayName("Should throw no such element exception when appointment list has no items")
    void convertEmptyItems() {
        // given
        AppointmentList appointmentList = new AppointmentList()
                .items(emptyList());

        when(alphaKeyMapper.makeSortKey(any())).thenReturn("natural officer2");

        // when
        Executable executable = () -> converter.convert(appointmentList);

        // then
        assertThrows(NoSuchElementException.class, executable);
        verify(alphaKeyMapper).makeSortKey(appointmentList);
        verifyNoInteractions(officerAppointmentSummaryConverter);
        verifyNoInteractions(appointmentAddressConverter);
    }
}
