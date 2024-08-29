package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.util.AddressUtils.getROAFullAddressString;
import static uk.gov.companieshouse.search.api.util.OfficerNameUtils.getCorporateNameEndings;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItem;

@Component
public class CompanySearchItemConverter implements Converter<CompanySearchItemConverterModel, CompanySearchItem> {

    private final ConversionService companySearchAddressConverter;

    public CompanySearchItemConverter(@Lazy ConversionService companySearchAddressConverter) {
        this.companySearchAddressConverter = companySearchAddressConverter;
    }

    @Override
    public CompanySearchItem convert(CompanySearchItemConverterModel model) {
        String renderedFullAddress = getROAFullAddressString(model.getRegisteredOfficeAddress());
        Pair<String, String> corporateNameEndings = getCorporateNameEndings(model.getCompanyName());

        if (!model.isPartialData() && model.getCeasedOn() == null) {
            return CompanySearchItem.Builder.builder()
                    .corporateNameStart(corporateNameEndings.getLeft())
                    .corporateNameEnding(corporateNameEndings.getRight())
                    .dateOfCreation(model.getDateOfCreation())
                    .fullAddress(renderedFullAddress)
                    .address(companySearchAddressConverter.convert(model.getRegisteredOfficeAddress(),
                            CompanySearchAddress.class))
                    .companyNumber(model.getCompanyNumber())
                    .externalRegistrationNumber(model.getExternalRegistrationNumber())
                    .dateOfCessation(model.getDateOfCessation())
                    .sicCodes(model.getSicCodes())
                    .companyStatus(model.getCompanyStatus())
                    .sameAsKey(StringUtils.removeEndIgnoreCase(model.getAlphaKey(), "s"))
                    .wildcardKey(getWildcardKey(model.getAlphaKey()))
                    .build();
        } else {
            return CompanySearchItem.Builder.builder()
                    .corporateNameStart(corporateNameEndings.getLeft())
                    .corporateNameEnding(corporateNameEndings.getRight())
                    .dateOfCreation(model.getDateOfCreation())
                    .fullAddress(renderedFullAddress)
                    .ceasedOn(model.getCeasedOn())
                    .build();
        }
    }

    private String getWildcardKey(String alphaKey) {
        if (alphaKey != null) {
            return alphaKey + "0";
        } else {
            return null;
        }
    }
}