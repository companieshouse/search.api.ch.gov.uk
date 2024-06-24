package uk.gov.companieshouse.search.api.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchDocument;

@Component
public class CompanySearchDocumentConverter implements Converter<Data, CompanySearchDocument> {

    @Override
    public CompanySearchDocument convert(Data source) {
        return null;
    }
}
