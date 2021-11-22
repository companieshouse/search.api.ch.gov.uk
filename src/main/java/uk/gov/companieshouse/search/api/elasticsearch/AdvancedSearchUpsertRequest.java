package uk.gov.companieshouse.search.api.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import org.elasticsearch.common.xcontent.XContentBuilder;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.RegisteredOfficeAddressApi;

import java.io.IOException;
import java.util.Map;

public class AdvancedSearchUpsertRequest {

    private static final String COMPANY_TYPE = "company_type";
    private static final String CURRENT_COMPANY = "current_company";
    private static final String ITEMS = "items";
    private static final String COMPANY_NUMBER = "company_number";
    private static final String COMPANY_STATUS = "company_status";
    private static final String CORPORATE_NAME = "corporate_name";
    private static final String CORPORATE_NAME_START = "corporate_name_start";
    private static final String CORPORATE_NAME_ENDING = "corporate_name_ending";
    private static final String RECORD_TYPE = "record_type";
    private static final String RECORD_TYPE_VALUE = "companies";
    private static final String LINKS = "links";
    private static final String SELF = "self";
    private static final String SIC_CODES = "sic_codes";
    private static final String ADDRESS = "address";
    private static final String PREMISES = "premises";
    private static final String ADDRESS_LINE_1 = "address_line_1";
    private static final String ADDRESS_LINE_2 = "address_line_2";
    private static final String POSTAL_CODE = "postal_code";
    private static final String LOCALITY = "locality";
    private static final String REGION = "region";
    private static final String COUNTRY = "country";
    private static final String WILDCARD_KEY = "wildcard_key";
    private static final String DATE_OF_CREATION = "date_of_creation";
    private static final String DATE_OF_CESSATION = "date_of_cessation";
    private static final String FULL_ADDRESS = "full_address";
    private static final String SAME_AS_KEY = "same_as_key";
    private static final String KIND = "kind";
    private static final String KIND_VALUE = "searchresults#company";
    private static final String SORT_KEY = "sort_key";

    public XContentBuilder buildRequest(
        CompanyProfileApi company,
        String orderedAlphaKey,
        String sameAsKey) throws IOException {

        RegisteredOfficeAddressApi registeredOfficeAddress = company.getRegisteredOfficeAddress();
        Map<String, String> links = company.getLinks();

        return jsonBuilder()
            .startObject()
                .field(COMPANY_TYPE, company.getType())
                .startObject(CURRENT_COMPANY)
                    .field(CORPORATE_NAME_START, company.getCompanyName())
                    .field(CORPORATE_NAME, company.getCompanyName())
                    .array(SIC_CODES, company.getSicCodes())
                    .field(COMPANY_NUMBER, company.getCompanyNumber())
                    .field(COMPANY_STATUS, company.getCompanyStatus())
                    .startObject(ADDRESS)
                        .field(PREMISES, registeredOfficeAddress.getPremises())
                        .field(ADDRESS_LINE_1, registeredOfficeAddress.getAddressLine1())
                        .field(ADDRESS_LINE_2, registeredOfficeAddress.getAddressLine2())
                        .field(POSTAL_CODE, registeredOfficeAddress.getPostalCode())
                        .field(LOCALITY, registeredOfficeAddress.getLocality())
                        .field(REGION, registeredOfficeAddress.getRegion())
                        .field(COUNTRY, registeredOfficeAddress.getCountry())
                    .endObject()
                    .field(WILDCARD_KEY, orderedAlphaKey)
                    .field(DATE_OF_CREATION, company.getDateOfCreation())
                    .field(DATE_OF_CESSATION, company.getDateOfCessation())
                    .field(FULL_ADDRESS, createFullAddress(registeredOfficeAddress))
                    .field(RECORD_TYPE, RECORD_TYPE_VALUE)
                    .field(SAME_AS_KEY, sameAsKey)
                    .field(CORPORATE_NAME_ENDING, company.getCompanyName())
                .endObject()
                .startObject(ITEMS)
                    .startObject(ADDRESS)
                        .field(PREMISES, registeredOfficeAddress.getPremises())
                        .field(ADDRESS_LINE_1, registeredOfficeAddress.getAddressLine1())
                        .field(ADDRESS_LINE_2, registeredOfficeAddress.getAddressLine2())
                        .field(POSTAL_CODE, registeredOfficeAddress.getPostalCode())
                        .field(LOCALITY, registeredOfficeAddress.getLocality())
                        .field(REGION, registeredOfficeAddress.getRegion())
                        .field(COUNTRY, registeredOfficeAddress.getCountry())
                    .endObject()
                    .field(COMPANY_NUMBER, company.getCompanyNumber())
                    .field(COMPANY_STATUS, company.getCompanyStatus())
                    .field(CORPORATE_NAME, company.getCompanyName())
                    .field(CORPORATE_NAME_START, company.getCompanyName())
                    .field(CORPORATE_NAME_ENDING, company.getCompanyName())
                    .array(SIC_CODES, company.getSicCodes())
                    .field(DATE_OF_CREATION, company.getDateOfCreation())
                    .field(DATE_OF_CESSATION, company.getDateOfCessation())
                    .field(FULL_ADDRESS, createFullAddress(registeredOfficeAddress))
                    .field(SAME_AS_KEY, sameAsKey)
                    .field(WILDCARD_KEY, orderedAlphaKey)
                    .field(RECORD_TYPE, RECORD_TYPE_VALUE)
                .endObject()
                .field(KIND, KIND_VALUE)
                .startObject(LINKS)
                    .field(SELF, links.get(SELF))
                .endObject()
                .field(SORT_KEY, orderedAlphaKey + "0")
            .endObject();
    }

    private String createFullAddress(RegisteredOfficeAddressApi registeredOfficeAddressApi) {
        StringBuilder fullAddressBuilder = new StringBuilder();

        appendAddressField(registeredOfficeAddressApi.getPremises(), fullAddressBuilder);
        appendAddressField(registeredOfficeAddressApi.getAddressLine1(), fullAddressBuilder);
        appendAddressField(registeredOfficeAddressApi.getAddressLine2(), fullAddressBuilder);
        appendAddressField(registeredOfficeAddressApi.getPostalCode(), fullAddressBuilder);
        appendAddressField(registeredOfficeAddressApi.getLocality(), fullAddressBuilder);
        appendAddressField(registeredOfficeAddressApi.getRegion(), fullAddressBuilder);
        appendAddressField(registeredOfficeAddressApi.getCountry(), fullAddressBuilder);

        String fullAddress = fullAddressBuilder.toString();

        if (fullAddress.charAt(fullAddress.length() - 1) == ',') {
            fullAddress = fullAddress.substring(0, fullAddress.length() - 1);
        }

        return fullAddress;
    }

    private void appendAddressField(String field, StringBuilder fullAddress) {
        if (field != null) {
            fullAddress.append(field);
            fullAddress.append(", ");
        }
    }
}


