package uk.gov.companieshouse.search.api.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.company.RegisteredOfficeAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchAddress;

@Component
public class CompanySearchAddressConverter implements Converter<RegisteredOfficeAddress, CompanySearchAddress> {

    @Override
    public CompanySearchAddress convert(RegisteredOfficeAddress registeredOfficeAddress) {
        return CompanySearchAddress.Builder.builder()
                .addressLine1(registeredOfficeAddress.getAddressLine1())
                .addressLine2(registeredOfficeAddress.getAddressLine2())
                .country(registeredOfficeAddress.getCountry())
                .locality(registeredOfficeAddress.getLocality())
                .poBox(registeredOfficeAddress.getPoBox())
                .postalCode(registeredOfficeAddress.getPostalCode())
                .premises(registeredOfficeAddress.getPremises())
                .region(registeredOfficeAddress.getRegion())
                .careOfName(registeredOfficeAddress.getCareOfName())
                .build();
    }
}
