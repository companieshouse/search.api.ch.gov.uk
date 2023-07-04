package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.officer.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.AppointmentAddress;

class AppointmentAddressConverterTest {

    private AppointmentAddressConverter converter;

    @BeforeEach
    void setUp() {
        converter = new AppointmentAddressConverter();
    }

    @Test
    void convert() {
        // given
        Address address = new Address()
                .addressLine1("address line 1")
                .addressLine2("address line 2")
                .careOf("care of")
                .country("country")
                .poBox("po box")
                .locality("locality")
                .postalCode("postal code")
                .premises("premises")
                .region("region");

        AppointmentAddress expected = AppointmentAddress.Builder.builder()
                .addressLine1("address line 1")
                .addressLine2("address line 2")
                .careOf("care of")
                .country("country")
                .poBox("po box")
                .locality("locality")
                .postalCode("postal code")
                .premises("premises")
                .region("region")
                .build();

        // when
        AppointmentAddress actual = converter.convert(address);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void convertNullValues() {
        // given
        AppointmentAddress expected = AppointmentAddress.Builder.builder()
                .build();

        // when
        AppointmentAddress actual = converter.convert(new Address());

        // then
        assertEquals(expected, actual);
    }
}