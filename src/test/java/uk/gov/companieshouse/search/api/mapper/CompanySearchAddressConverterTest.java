package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.company.RegisteredOfficeAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchAddress;

class CompanySearchAddressConverterTest {

    private CompanySearchAddressConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CompanySearchAddressConverter();
    }

    @Test
    void convert() {
        // given
        RegisteredOfficeAddress registeredOfficeAddress = new RegisteredOfficeAddress()
                .addressLine1("address line 1")
                .addressLine2("address line 2")
                .country("country")
                .poBox("po box")
                .locality("locality")
                .postalCode("postal code")
                .premises("premises")
                .region("region")
                .careOfName("care of name");

        CompanySearchAddress expected = CompanySearchAddress.Builder.builder()
                .addressLine1("address line 1")
                .addressLine2("address line 2")
                .country("country")
                .poBox("po box")
                .locality("locality")
                .postalCode("postal code")
                .premises("premises")
                .region("region")
                .careOfName("care of name")
                .build();

        // when
        CompanySearchAddress actual = converter.convert(registeredOfficeAddress);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void convertNullValues() {
        // given
        CompanySearchAddress expected = CompanySearchAddress.Builder.builder()
                .build();

        // when
        CompanySearchAddress actual = converter.convert(new RegisteredOfficeAddress());

        // then
        assertEquals(expected, actual);
    }
}