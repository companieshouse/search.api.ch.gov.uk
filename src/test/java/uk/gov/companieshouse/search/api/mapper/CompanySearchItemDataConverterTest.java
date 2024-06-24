package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanyItemDataConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemData;

public class CompanySearchItemDataConverterTest {

    private CompanySearchItemDataConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CompanySearchItemDataConverter();
    }

    @Test
    void convert() {
        // given
        CompanyItemDataConverterModel model = new CompanyItemDataConverterModel()
                .companyName("TEST PLC")
                .ceasedOn(LocalDate.of(2024, 6, 24))
                .dateOfCreation(LocalDate.of(2021, 6, 24))
                .fullAddress("C/O 123 Test Street A12 B34");

        CompanySearchItemData expected = CompanySearchItemData.Builder.builder()
                .corporateNameStart("TEST")
                .corporateNameEnding("PLC")
                .ceasedOn(LocalDate.of(2024, 6, 24))
                .dateOfCreation(LocalDate.of(2021, 6, 24))
                .fullAddress("C/O 123 Test Street A12 B34")
                .recordType("companies")
                .build();

        // when
        CompanySearchItemData actual = converter.convert(model);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void convertWithNullValues() {
        // given
        CompanyItemDataConverterModel model = new CompanyItemDataConverterModel()
                .companyName("TEST PLC");

        CompanySearchItemData expected = CompanySearchItemData.Builder.builder()
                .corporateNameStart("TEST")
                .corporateNameEnding("PLC")
                .build();

        // when
        CompanySearchItemData actual = converter.convert(model);

        // then
        assertEquals(expected, actual);
    }

}
