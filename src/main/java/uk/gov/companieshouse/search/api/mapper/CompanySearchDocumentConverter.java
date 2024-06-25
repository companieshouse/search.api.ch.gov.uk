package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.util.AddressUtils.getROAFullAddressString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanyItemDataConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanyItemFullDataConverterModel;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemData;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchItemFullData;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchDocument;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchLinks;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

@Component
public class CompanySearchDocumentConverter implements Converter<Data, CompanySearchDocument> {

    private final ConversionService companySearchItemDataConverter;

    private final ConversionService companySearchItemFullDataConverter;

    private final AlphaKeyService alphaKeyService;

    public CompanySearchDocumentConverter(@Lazy ConversionService companySearchItemDataConverter,
            @Lazy ConversionService companySearchItemFullDataConverter, AlphaKeyService alphaKeyService) {
        this.companySearchItemDataConverter = companySearchItemDataConverter;
        this.companySearchItemFullDataConverter = companySearchItemFullDataConverter;
        this.alphaKeyService = alphaKeyService;
    }

    @Override
    public CompanySearchDocument convert(Data data) {
        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(data.getCompanyName());
        String alphaKey = alphaKeyResponse.getOrderedAlphaKey();

        String renderedFullAddress = getROAFullAddressString(data.getRegisteredOfficeAddress());
        LocalDate dateOfCreation = data.getDateOfCreation();

        CompanySearchItemFullData firstItem = companySearchItemFullDataConverter.convert(
                new CompanyItemFullDataConverterModel()
                        .companySearchData(companySearchItemDataConverter.convert(
                                new CompanyItemDataConverterModel()
                                        .companyName(data.getCompanyName())
                                        .dateOfCreation(dateOfCreation)
                                        .fullAddress(renderedFullAddress), CompanySearchItemData.class))
                        .companyData(data)
                        .alphaKey(alphaKey),
                CompanySearchItemFullData.class);

        List<CompanySearchItemFullData> items = new ArrayList<>();
        items.add(firstItem);

        if (data.getPreviousCompanyNames() != null) {
            items.addAll(data.getPreviousCompanyNames().stream().map(
                    previousName -> companySearchItemFullDataConverter.convert(
                            new CompanyItemFullDataConverterModel()
                                    .companySearchData(companySearchItemDataConverter.convert(
                                            new CompanyItemDataConverterModel()
                                                    .fullAddress(renderedFullAddress)
                                                    .ceasedOn(previousName.getCeasedOn())
                                                    .dateOfCreation(dateOfCreation)
                                                    .companyName(previousName.getName()), CompanySearchItemData.class)),
                            CompanySearchItemFullData.class)
            ).collect(Collectors.toList()));
        }

        return CompanySearchDocument.Builder.builder()
                .items(items)
                .companyType(data.getType())
                .sortKey(alphaKey + "0")
                .links(new CompanySearchLinks(data.getLinks().getSelf()))
                .build();
    }
}
