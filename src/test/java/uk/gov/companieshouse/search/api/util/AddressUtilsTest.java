package uk.gov.companieshouse.search.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.companieshouse.search.api.util.AddressUtils.getFullAddressString;
import static uk.gov.companieshouse.search.api.util.AddressUtils.getROAFullAddressString;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.company.RegisteredOfficeAddress;
import uk.gov.companieshouse.api.officer.Address;

class AddressUtilsTest {

    private static final String CARE_OF = "care of";
    private static final String PO_BOX = "po box";
    private static final String PREMISES = "100";
    private static final String ADDRESS_LINE_1 = "addressLine1";
    private static final String ADDRESS_LINE_2 = "addressLine2";
    private static final String LOCALITY = "locality";
    private static final String REGION = "region";
    private static final String COUNTRY = "country";
    private static final String POSTAL_CODE = "postcode";
    private static final String CARE_OF_NAME = "care of name";

    @Test
    void getAddressAsStringReturnAddressStringNumberPremise() {
        Address address = getAddress();

        String addressLine = getFullAddressString(address);

        assertThat(addressLine).isEqualTo(CARE_OF + ", " + PO_BOX + ", " + PREMISES + " "
                + ADDRESS_LINE_1 + ", " + ADDRESS_LINE_2 + ", " + LOCALITY + ", " + REGION
                + ", " + COUNTRY + ", " + POSTAL_CODE);
    }

    @Test
    void getAddressAsStringReturnAddressStringWordPremise() {
        String premises = "Test";
        Address address = getAddress()
                .premises(premises);

        String addressLine = getFullAddressString(address);

        assertThat(addressLine).isEqualTo(CARE_OF + ", " + PO_BOX + ", " + premises + ", "
                + ADDRESS_LINE_1 + ", " + ADDRESS_LINE_2 + ", " + LOCALITY + ", " + REGION
                + ", " + COUNTRY + ", " + POSTAL_CODE);
    }

    @Test
    void getAddressAsStringReturnAddressStringNoPremise() {
        String premises = "";
        Address address = getAddress()
                .premises(premises);

        String addressLine = getFullAddressString(address);

        assertThat(addressLine).isEqualTo(CARE_OF + ", " + PO_BOX + ", "
                + ADDRESS_LINE_1 + ", " + ADDRESS_LINE_2 + ", " + LOCALITY + ", " + REGION
                + ", " + COUNTRY + ", " + POSTAL_CODE);
    }

    @Test
    void getAddressAsStringReturnAddressStringNoAddressLine1() {
        String addressLine1 = "";
        Address address = getAddress()
                .addressLine1(addressLine1);

        String addressLine = getFullAddressString(address);

        assertThat(addressLine).isEqualTo(CARE_OF + ", " + PO_BOX + ", " + PREMISES
                + ", " + ADDRESS_LINE_2 + ", " + LOCALITY + ", " + REGION
                + ", " + COUNTRY + ", " + POSTAL_CODE);
    }

    @Test
    void getROAsStringReturnAddressStringNumberPremise() {
        RegisteredOfficeAddress address = getROAddress();

        String addressLine = getROAFullAddressString(address);

        assertThat(addressLine).isEqualTo(CARE_OF_NAME + ", " + PO_BOX + ", " + PREMISES + " "
                + ADDRESS_LINE_1 + ", " + ADDRESS_LINE_2 + ", " + LOCALITY + ", " + REGION
                + ", " + COUNTRY + ", " + POSTAL_CODE);
    }

    @Test
    void getROAAsStringReturnAddressStringWordPremise() {
        String premises = "Test";
        RegisteredOfficeAddress address = getROAddress()
                .premises(premises);

        String addressLine = getROAFullAddressString(address);

        assertThat(addressLine).isEqualTo(CARE_OF_NAME + ", " + PO_BOX + ", " + premises + ", "
                + ADDRESS_LINE_1 + ", " + ADDRESS_LINE_2 + ", " + LOCALITY + ", " + REGION
                + ", " + COUNTRY + ", " + POSTAL_CODE);
    }

    @Test
    void getROAAsStringReturnAddressStringNoPremise() {
        String premises = "";
        RegisteredOfficeAddress address = getROAddress()
                .premises(premises);

        String addressLine = getROAFullAddressString(address);

        assertThat(addressLine).isEqualTo(CARE_OF_NAME + ", " + PO_BOX + ", "
                + ADDRESS_LINE_1 + ", " + ADDRESS_LINE_2 + ", " + LOCALITY + ", " + REGION
                + ", " + COUNTRY + ", " + POSTAL_CODE);
    }

    @Test
    void getROAAsStringReturnAddressStringNoAddressLine1() {
        String addressLine1 = "";
        RegisteredOfficeAddress address = getROAddress()
                .addressLine1(addressLine1);

        String addressLine = getROAFullAddressString(address);

        assertThat(addressLine).isEqualTo(CARE_OF_NAME + ", " + PO_BOX + ", " + PREMISES
                + ", " + ADDRESS_LINE_2 + ", " + LOCALITY + ", " + REGION
                + ", " + COUNTRY + ", " + POSTAL_CODE);
    }

    private Address getAddress() {
        return new Address()
                .addressLine1(ADDRESS_LINE_1)
                .addressLine2(ADDRESS_LINE_2)
                .region(REGION)
                .country(COUNTRY)
                .careOf(CARE_OF)
                .poBox(PO_BOX)
                .postalCode(POSTAL_CODE)
                .premises(PREMISES)
                .locality(LOCALITY);
    }

    private RegisteredOfficeAddress getROAddress() {
        return new RegisteredOfficeAddress()
                .addressLine1(ADDRESS_LINE_1)
                .addressLine2(ADDRESS_LINE_2)
                .region(REGION)
                .country(COUNTRY)
                .poBox(PO_BOX)
                .postalCode(POSTAL_CODE)
                .premises(PREMISES)
                .locality(LOCALITY)
                .careOfName(CARE_OF_NAME);
    }
}
