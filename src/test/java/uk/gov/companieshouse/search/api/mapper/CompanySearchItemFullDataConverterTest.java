package uk.gov.companieshouse.search.api.mapper;

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
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.company.RegisteredOfficeAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanyItemFullDataConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemData;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemFullData;

@ExtendWith(MockitoExtension.class)
public class CompanySearchItemFullDataConverterTest {

    CompanySearchItemFullDataConverter converter;

    @Mock
    private ConversionService companySearchAddressConverter;

    @BeforeEach
    void setUp() {
        converter = new CompanySearchItemFullDataConverter(companySearchAddressConverter);
    }

    @Test
    void convert() {
        // given
        CompanyItemFullDataConverterModel model = new CompanyItemFullDataConverterModel()
                .companySearchData(CompanySearchItemData.Builder.builder().build())
                .companyData(new Data()
                        .registeredOfficeAddress(new RegisteredOfficeAddress())
                        .companyNumber("ABCD1234")
                        .externalRegistrationNumber("CDEF3456")
                        .dateOfCessation(LocalDate.of(2024, 6, 24))
                        .sicCodes(Arrays.asList("12345", "23456", "34567"))
                        .companyStatus("active"))
                .alphaKey("TESTCOMPANY");

        CompanySearchItemFullData expected = CompanySearchItemFullData.Builder.builder()
                .companySearchData(CompanySearchItemData.Builder.builder().build())
                .address(CompanySearchAddress.Builder.builder().build())
                .companyNumber("ABCD1234")
                .externalRegistrationNumber("CDEF3456")
                .dateOfCessation(LocalDate.of(2024, 6, 24))
                .sicCodes(Arrays.asList("12345", "23456", "34567"))
                .companyStatus("active")
                .sameAsKey("TESTCOMPANY")
                .wildcardKey("TESTCOMPANY0")
                .build();

        when(companySearchAddressConverter.convert(any(), eq(CompanySearchAddress.class))).thenReturn(
                CompanySearchAddress.Builder.builder().build());

        // when
        CompanySearchItemFullData actual = converter.convert(model);

        //then
        assertEquals(expected, actual);
        verify(companySearchAddressConverter).convert(model.getCompanyData().getRegisteredOfficeAddress(), CompanySearchAddress.class);
    }

    @Test
    void convertWithNullValues() {
        // given
        CompanyItemFullDataConverterModel model = new CompanyItemFullDataConverterModel()
                .companyData(new Data().registeredOfficeAddress(new RegisteredOfficeAddress()));

        CompanySearchItemFullData expected = CompanySearchItemFullData.Builder.builder().build();

        // when
        CompanySearchItemFullData actual = converter.convert(model);

        //then
        assertEquals(expected, actual);
    }
}