package uk.gov.companieshouse.search.api.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItem;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchDocument;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchLinks;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

@Component
public class CompanySearchDocumentConverter implements Converter<Data, CompanySearchDocument> {

    private final ConversionService companySearchItemConverter;

    private final AlphaKeyService alphaKeyService;

    public CompanySearchDocumentConverter(@Lazy ConversionService companySearchItemConverter,
            AlphaKeyService alphaKeyService) {
        this.companySearchItemConverter = companySearchItemConverter;
        this.alphaKeyService = alphaKeyService;
    }

    @Override
    public CompanySearchDocument convert(Data data) {
        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(data.getCompanyName());
        String alphaKey = alphaKeyResponse.getOrderedAlphaKey();

        CompanySearchItem firstItem = companySearchItemConverter.convert(
                new CompanySearchItemConverterModel()
                        .partialData(false)
                        .companyName(data.getCompanyName())
                        .dateOfCreation(data.getDateOfCreation())
                        .registeredOfficeAddress(data.getRegisteredOfficeAddress())
                        .companyNumber(data.getCompanyNumber())
                        .externalRegistrationNumber(data.getExternalRegistrationNumber())
                        .dateOfCessation(data.getDateOfCessation())
                        .sicCodes(data.getSicCodes())
                        .companyStatus(data.getCompanyStatus())
                        .alphaKey(alphaKey)
                        .sameAsKey(alphaKeyResponse.getSameAsAlphaKey()),
                CompanySearchItem.class);

        List<CompanySearchItem> items = new ArrayList<>();
        items.add(firstItem);

        if (data.getPreviousCompanyNames() != null) {
            items.addAll(data.getPreviousCompanyNames().stream().map(
                    previousName -> companySearchItemConverter.convert(
                            new CompanySearchItemConverterModel()
                                    .partialData(true)
                                    .companyName(previousName.getName())
                                    .ceasedOn(previousName.getCeasedOn())
                                    .dateOfCreation(data.getDateOfCreation())
                                    .registeredOfficeAddress(data.getRegisteredOfficeAddress()),
                            CompanySearchItem.class))
                    .collect(Collectors.toList()));
        }

        return CompanySearchDocument.Builder.builder()
                .items(items)
                .companyType(data.getType())
                .sortKey(alphaKey + "0")
                .links(new CompanySearchLinks(data.getLinks().getSelf()))
                .build();
    }
}
