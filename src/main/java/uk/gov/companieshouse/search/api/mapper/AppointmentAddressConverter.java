package uk.gov.companieshouse.search.api.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.AppointmentAddress;

@Component
public class AppointmentAddressConverter implements Converter<Address, AppointmentAddress> {

    @Override
    public AppointmentAddress convert(Address appointmentListAddress) {
        return AppointmentAddress.Builder.builder()
                .addressLine1(appointmentListAddress.getAddressLine1())
                .addressLine2(appointmentListAddress.getAddressLine2())
                .careOf(appointmentListAddress.getCareOf())
                .country(appointmentListAddress.getCountry())
                .locality(appointmentListAddress.getLocality())
                .poBox(appointmentListAddress.getPoBox())
                .postalCode(appointmentListAddress.getPostalCode())
                .premises(appointmentListAddress.getPremises())
                .region(appointmentListAddress.getRegion())
                .build();
    }

    @Override
    public <U> Converter<Address, U> andThen(Converter<? super AppointmentAddress, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
