package uk.gov.companieshouse.search.api.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.gov.companieshouse.api.officer.Address;

public class AddressUtils {

    public static String getFullAddressString(Address address) {
        return Stream.of(address.getCareOf(), address.getPoBox(), getPremiseAddressLine1(address), address.getAddressLine2(),
                address.getLocality(), address.getRegion(), address.getCountry(), address.getPostalCode())
                .filter(AddressUtils::checkString)
                .collect(Collectors.joining(", "));
    }

    private static String getPremiseAddressLine1(Address a) {
        String premises = a.getPremises();
        String addressLine1 = a.getAddressLine1();

        if (! checkString(premises)) return addressLine1;
        else if (! checkString(addressLine1)) return premises;
        else {
            if (premises.matches("^\\d+$")) {
                return premises + " " + addressLine1;
            } else {
                return premises + ", " + addressLine1;
            }
        }
    }

    private static boolean checkString(String s) {
        return s != null && s.length() > 0;
    }
}
