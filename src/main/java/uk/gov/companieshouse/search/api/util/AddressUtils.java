package uk.gov.companieshouse.search.api.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.gov.companieshouse.api.company.RegisteredOfficeAddress;
import uk.gov.companieshouse.api.officer.Address;

public class AddressUtils {

    private AddressUtils() {
    }

    public static String getFullAddressString(Address address) {
        return Stream.of(address.getCareOf(), address.getPoBox(), getPremisesOfficers(address), address.getAddressLine2(),
                address.getLocality(), address.getRegion(), address.getCountry(), address.getPostalCode())
                .filter(AddressUtils::checkString)
                .collect(Collectors.joining(", "));
    }

    public static String getROAFullAddressString(RegisteredOfficeAddress address) {
        return Stream.of(address.getCareOfName(), address.getPoBox(), getPremisesCompany(address), address.getAddressLine2(),
                        address.getLocality(), address.getRegion(), address.getCountry(), address.getPostalCode())
                .filter(AddressUtils::checkString)
                .collect(Collectors.joining(", "));
    }

    private static String getPremisesOfficers(Address a) {
        String premises = a.getPremises();
        String addressLine1 = a.getAddressLine1();
        return getPremiseAddressLine1(premises, addressLine1);
    }

    private static String getPremisesCompany(RegisteredOfficeAddress a) {
        String premises = a.getPremises();
        String addressLine1 = a.getAddressLine1();
        return getPremiseAddressLine1(premises, addressLine1);
    }

    private static String getPremiseAddressLine1(String premises, String addressLine1) {
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
