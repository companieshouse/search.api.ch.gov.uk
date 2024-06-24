package uk.gov.companieshouse.search.api.mapper;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanyItemFullDataConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchAddress;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemFullData;

@Component
public class CompanySearchItemFullDataConverter implements Converter<CompanyItemFullDataConverterModel, CompanySearchItemFullData> {

    private final ConversionService companySearchAddressConverter;

    public CompanySearchItemFullDataConverter(@Lazy ConversionService companySearchAddressConverter) {
        this.companySearchAddressConverter = companySearchAddressConverter;
    }

    @Override
    public CompanySearchItemFullData convert(CompanyItemFullDataConverterModel model) {
        Data profileData = model.getCompanyData();
        return CompanySearchItemFullData.Builder.builder()
                .companySearchData(model.getCompanySearchRequiredData())
                .address(companySearchAddressConverter.convert(profileData.getRegisteredOfficeAddress(),
                        CompanySearchAddress.class))
                .companyNumber(profileData.getCompanyNumber())
                .externalRegistrationNumber(profileData.getExternalRegistrationNumber())
                .dateOfCessation(profileData.getDateOfCessation())
                .sicCodes(profileData.getSicCodes())
                .companyStatus(profileData.getCompanyStatus())
                .sameAsKey(model.getAlphaKey())
                .wildcardKey(getWildcardKey(model.getAlphaKey()))
                .build();
    }

    private String getWildcardKey(String alphaKey) {
        if (alphaKey != null) {
            return alphaKey + "0";
        } else {
            return null;
        }
    }
}