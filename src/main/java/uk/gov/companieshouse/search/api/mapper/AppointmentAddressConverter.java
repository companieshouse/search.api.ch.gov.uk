package uk.gov.companieshouse.search.api.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.AppointmentAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.AppointmentAddress.Builder;

@Component
public class AppointmentAddressConverter implements Converter<Address, AppointmentAddress>{

    @Override
    public AppointmentAddress convert(Address appointmentListAddress) {
        return new AppointmentAddress(
                Builder.builder(
                appointmentListAddress.getAddressLine1(),
                appointmentListAddress.getAddressLine2(),
                appointmentListAddress.getCareOf(),
                appointmentListAddress.getCountry(),
                appointmentListAddress.getLocality(),
                appointmentListAddress.getPoBox(),
                appointmentListAddress.getPostalCode(),
                appointmentListAddress.getPremises(),
                appointmentListAddress.getRegion()
                ));
    }

    @Override
    public <U> Converter<Address, U> andThen(Converter<? super AppointmentAddress, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
