package uk.gov.companieshouse.search.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import uk.gov.companieshouse.api.company.RegisteredOfficeAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItem;

@ExtendWith(MockitoExtension.class)
class CompanySearchItemConverterTest {

    private CompanySearchItemConverter converter;

    @Mock
    private ConversionService companySearchAddressConverter;

    @BeforeEach
    void setUp() {
        converter = new CompanySearchItemConverter(companySearchAddressConverter);
    }

    @Test
    void convertFullData() {
        // given
        CompanySearchItemConverterModel model = new CompanySearchItemConverterModel()
                .partialData(false)
                .companyName("TEST COMPANY PLC")
                .dateOfCreation(LocalDate.of(2000, 6, 24))
                .registeredOfficeAddress(getROAProperites())
                .companyNumber("ABCD1234")
                .externalRegistrationNumber("CDEF3456")
                .dateOfCessation(LocalDate.of(2024, 6, 24))
                .sicCodes(Arrays.asList("12345", "23456", "34567"))
                .companyStatus("active")
                .alphaKey("TESTCOMPANYS");

        CompanySearchItem expected = CompanySearchItem.Builder.builder()
                .corporateNameStart("TEST COMPANY")
                .corporateNameEnding("PLC")
                .dateOfCreation(LocalDate.of(2000, 6, 24))
                .fullAddress("care of name, po box, premises, address line 1, address line 2, "
                        + "locality, region, country, postal code")
                .address(CompanySearchAddress.Builder.builder().build())
                .companyNumber("ABCD1234")
                .externalRegistrationNumber("CDEF3456")
                .dateOfCessation(LocalDate.of(2024, 6, 24))
                .sicCodes(Arrays.asList("12345", "23456", "34567"))
                .companyStatus("active")
                .sameAsKey("TESTCOMPANY")
                .wildcardKey("TESTCOMPANYS0")
                .build();

        when(companySearchAddressConverter.convert(any(), eq(CompanySearchAddress.class))).thenReturn(
                CompanySearchAddress.Builder.builder().build());

        // when
        CompanySearchItem actual = converter.convert(model);

        //then
        assertEquals(expected, actual);
        verify(companySearchAddressConverter).convert(
                model.getRegisteredOfficeAddress(), CompanySearchAddress.class);
    }

    @Test
    void convertPartialDataFalseAndCeasedOn() {
        // given
        CompanySearchItemConverterModel model = new CompanySearchItemConverterModel()
                .partialData(false)
                .companyName("PAST COMPANY PLC")
                .ceasedOn(LocalDate.of(2022, 6, 24))
                .dateOfCreation(LocalDate.of(2000, 6, 24))
                .registeredOfficeAddress(getROAProperites());

        CompanySearchItem expected = CompanySearchItem.Builder.builder()
                .corporateNameStart("PAST COMPANY")
                .corporateNameEnding("PLC")
                .dateOfCreation(LocalDate.of(2000, 6, 24))
                .fullAddress("care of name, po box, premises, address line 1, address line 2, "
                        + "locality, region, country, postal code")
                .ceasedOn(LocalDate.of(2022, 6, 24))
                .build();

        // when
        CompanySearchItem actual = converter.convert(model);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void convertPartialData() {
        // given
        CompanySearchItemConverterModel model = new CompanySearchItemConverterModel()
                .partialData(true)
                .companyName("PAST COMPANY PLC")
                .ceasedOn(LocalDate.of(2022, 6, 24))
                .dateOfCreation(LocalDate.of(2000, 6, 24))
                .registeredOfficeAddress(getROAProperites());

        CompanySearchItem expected = CompanySearchItem.Builder.builder()
                .corporateNameStart("PAST COMPANY")
                .corporateNameEnding("PLC")
                .dateOfCreation(LocalDate.of(2000, 6, 24))
                .fullAddress("care of name, po box, premises, address line 1, address line 2, "
                        + "locality, region, country, postal code")
                .ceasedOn(LocalDate.of(2022, 6, 24))
                .build();

        // when
        CompanySearchItem actual = converter.convert(model);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void convertWithNullValues() {
        // given
        CompanySearchItemConverterModel model = new CompanySearchItemConverterModel()
                .companyName("TEST COMPANY PLC")
                .registeredOfficeAddress(getROAProperites());

        CompanySearchItem expected = CompanySearchItem.Builder.builder()
                .corporateNameStart("TEST COMPANY")
                .corporateNameEnding("PLC")
                .fullAddress("care of name, po box, premises, address line 1, address line 2, "
                        + "locality, region, country, postal code")
                .build();

        // when
        CompanySearchItem actual = converter.convert(model);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void convertWithNullRoa() {
        // given
        CompanySearchItemConverterModel model = new CompanySearchItemConverterModel()
                .partialData(false)
                .companyName("TEST COMPANY PLC")
                .dateOfCreation(LocalDate.of(2000, 6, 24))
                .registeredOfficeAddress(null)
                .companyNumber("ABCD1234")
                .externalRegistrationNumber("CDEF3456")
                .dateOfCessation(LocalDate.of(2024, 6, 24))
                .sicCodes(Arrays.asList("12345", "23456", "34567"))
                .companyStatus("active")
                .alphaKey("TESTCOMPANYS");

        CompanySearchItem expected = CompanySearchItem.Builder.builder()
                .corporateNameStart("TEST COMPANY")
                .corporateNameEnding("PLC")
                .dateOfCreation(LocalDate.of(2000, 6, 24))
                .fullAddress("")
                .address(null)
                .companyNumber("ABCD1234")
                .externalRegistrationNumber("CDEF3456")
                .dateOfCessation(LocalDate.of(2024, 6, 24))
                .sicCodes(Arrays.asList("12345", "23456", "34567"))
                .companyStatus("active")
                .sameAsKey("TESTCOMPANY")
                .wildcardKey("TESTCOMPANYS0")
                .build();

        // when
        CompanySearchItem actual = converter.convert(model);

        //then
//        assertEquals(expected, actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
        verify(companySearchAddressConverter).convert(
                new RegisteredOfficeAddress(), CompanySearchAddress.class);
    }

    private RegisteredOfficeAddress getROAProperites() {
        return new RegisteredOfficeAddress()
                .addressLine1("address line 1")
                .addressLine2("address line 2")
                .country("country")
                .poBox("po box")
                .locality("locality")
                .postalCode("postal code")
                .premises("premises")
                .region("region")
                .careOfName("care of name");
    }
}