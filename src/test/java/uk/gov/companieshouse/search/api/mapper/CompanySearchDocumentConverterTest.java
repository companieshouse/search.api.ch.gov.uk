package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.company.Links;
import uk.gov.companieshouse.api.company.PreviousCompanyNames;
import uk.gov.companieshouse.api.company.RegisteredOfficeAddress;;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchDocument;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemData;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemFullData;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchLinks;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

@ExtendWith(MockitoExtension.class)
class CompanySearchDocumentConverterTest {

    private CompanySearchDocumentConverter converter;

    @Mock
    private ConversionService companySearchItemDataConverter;

    @Mock
    private ConversionService companySearchItemFullDataConverter;

    @Mock
    private AlphaKeyService alphaKeyService;

    @Mock
    private AlphaKeyResponse alphaKeyResponse;

    @BeforeEach
    void setUp() {
        converter = new CompanySearchDocumentConverter(companySearchItemDataConverter,
                companySearchItemFullDataConverter, alphaKeyService);
    }

    @Test
    void convert() {
        // given
        Data source = getProfileData();
        CompanySearchDocument expected = CompanySearchDocument.Builder.builder()
                .items(Arrays.asList(
                        CompanySearchItemFullData.Builder.builder().build(),
                        CompanySearchItemFullData.Builder.builder().build(),
                        CompanySearchItemFullData.Builder.builder().build(),
                        CompanySearchItemFullData.Builder.builder().build()
                ))
                .companyType("plc")
                .sortKey("TESTCOMPANYPLC0")
                .links(new CompanySearchLinks("/company/ABCD1234"))
                .build();;

        when(alphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(alphaKeyResponse);
        when(alphaKeyResponse.getOrderedAlphaKey()).thenReturn("TESTCOMPANYPLC");
        when(companySearchItemFullDataConverter.convert(any(), eq(CompanySearchItemFullData.class)))
                .thenReturn(CompanySearchItemFullData.Builder.builder().build());
        when(companySearchItemDataConverter.convert(any(), eq(CompanySearchItemData.class)))
                .thenReturn(CompanySearchItemData.Builder.builder().build());

        // when
        CompanySearchDocument actual = converter.convert(source);

        // then
        assertEquals(expected, actual);
        verify(alphaKeyService).getAlphaKeyForCorporateName("TEST COMPANY PLC");
        verify(companySearchItemFullDataConverter, times(4)).convert(any(),
                eq(CompanySearchItemFullData.class));
        verify(companySearchItemDataConverter, times(4)).convert(any(),
                eq(CompanySearchItemData.class));
    }

    @Test
    void convertWithNullValues() {
        // given
        List<CompanySearchItemFullData> items = new ArrayList<>();
        items.add(null);

        Data source = new Data()
                .registeredOfficeAddress(getROASource())
                .companyName("TEST COMPANY PLC")
                .links(new Links().self("links"));
        CompanySearchDocument expected = CompanySearchDocument.Builder.builder()
                .items(items)
                .sortKey("TESTCOMPANYPLC0")
                .links(new CompanySearchLinks("links"))
                .build();


        when(alphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(alphaKeyResponse);
        when(alphaKeyResponse.getOrderedAlphaKey()).thenReturn("TESTCOMPANYPLC");

        // when
        CompanySearchDocument actual = converter.convert(source);

        // then
        assertEquals(expected, actual);
    }

    private Data getProfileData() {
        return new Data()
                .registeredOfficeAddress(getROASource())
                .companyName("TEST COMPANY PLC")
                .companyNumber("ABCD1234")
                .externalRegistrationNumber("CDEF3456")
                .dateOfCreation(LocalDate.of(2010, 6, 24))
                .dateOfCessation(LocalDate.of(2024, 6, 24))
                .sicCodes(Arrays.asList("12345", "23456", "34567"))
                .companyStatus("active")
                .previousCompanyNames(Arrays.asList(
                        new PreviousCompanyNames()
                                .name("ABC COMPANY PLC")
                                .ceasedOn(LocalDate.of(2019, 6, 24)),
                        new PreviousCompanyNames()
                                .name("BCD COMPANY PLC")
                                .ceasedOn(LocalDate.of(2017, 6, 24)),
                        new PreviousCompanyNames()
                                .name("CDE COMPANY PLC")
                                .ceasedOn(LocalDate.of(2015, 6, 24))))
                .type("plc")
                .links(new Links().self("/company/ABCD1234"));
    }

    private RegisteredOfficeAddress getROASource() {
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