package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.util.OfficerNameUtils.getCorporateNameEndings;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanyItemDataConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemData;

@Component
public class CompanySearchItemDataConverter implements Converter<CompanyItemDataConverterModel, CompanySearchItemData> {

    @Override
    public CompanySearchItemData convert(CompanyItemDataConverterModel model) {
        Pair<String, String> corporateNameEndings = getCorporateNameEndings(model.getCompanyName());
        return CompanySearchItemData.Builder.builder()
                .corporateNameStart(corporateNameEndings.getLeft())
                .corporateNameEnding(corporateNameEndings.getRight())
                .ceasedOn(model.getCeasedOn())
                .dateOfCreation(model.getDateOfCreation())
                .fullAddress(model.getFullAddress())
                .build();
    }
}