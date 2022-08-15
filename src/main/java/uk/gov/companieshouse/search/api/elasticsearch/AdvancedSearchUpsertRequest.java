package uk.gov.companieshouse.search.api.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.RegisteredOfficeAddressApi;

import java.io.IOException;
import java.util.Map;

@Component
public class AdvancedSearchUpsertRequest {

    private static final String COMPANY_TYPE_KEY = "company_type";
    private static final String COMPANY_SUBTYPE_KEY = "company_subtype";
    private static final String CURRENT_COMPANY_KEY = "current_company";
    private static final String ITEMS_KEY = "items";
    private static final String COMPANY_NUMBER_KEY = "company_number";
    private static final String COMPANY_STATUS_KEY = "company_status";
    private static final String CORPORATE_NAME_KEY = "corporate_name";
    private static final String RECORD_TYPE_KEY = "record_type";
    private static final String RECORD_TYPE_VALUE = "companies";
    private static final String LINKS_KEY = "links";
    private static final String SELF_KEY = "self";
    private static final String SIC_CODES_KEY = "sic_codes";
    private static final String ADDRESS_KEY = "address";
    private static final String PREMISES_KEY = "premises";
    private static final String ADDRESS_LINE_1_KEY = "address_line_1";
    private static final String ADDRESS_LINE_2_KEY = "address_line_2";
    private static final String POSTAL_CODE_KEY = "postal_code";
    private static final String LOCALITY_KEY = "locality";
    private static final String REGION_KEY = "region";
    private static final String COUNTRY_KEY = "country";
    private static final String WILDCARD_KEY = "wildcard_key";
    private static final String DATE_OF_CREATION_KEY = "date_of_creation";
    private static final String DATE_OF_CESSATION_KEY = "date_of_cessation";
    private static final String FULL_ADDRESS_KEY = "full_address";
    private static final String SAME_AS_KEY = "same_as_key";
    private static final String KIND_KEY = "kind";
    private static final String KIND_VALUE = "searchresults#company";
    private static final String SORT_KEY = "sort_key";

    public XContentBuilder buildRequest(
        CompanyProfileApi company,
        String orderedAlphaKey,
        String sameAsKey) throws IOException {

        RegisteredOfficeAddressApi registeredOfficeAddress = company.getRegisteredOfficeAddress();
        Map<String, String> links = company.getLinks();

        XContentBuilder jsonBuilder = jsonBuilder().startObject();
        jsonBuilder.field(COMPANY_TYPE_KEY, company.getType());
        jsonBuilder.field(COMPANY_SUBTYPE_KEY, company.getSubtype());
            jsonBuilder.startObject(CURRENT_COMPANY_KEY);
                jsonBuilder.field(CORPORATE_NAME_KEY, company.getCompanyName());
                jsonBuilder.array(SIC_CODES_KEY, company.getSicCodes());
                jsonBuilder.field(COMPANY_NUMBER_KEY, company.getCompanyNumber());
                jsonBuilder.field(COMPANY_STATUS_KEY, company.getCompanyStatus());
                jsonBuilder.startObject(ADDRESS_KEY);
                    buildAddressJSON(jsonBuilder, registeredOfficeAddress);
                jsonBuilder.endObject();
                jsonBuilder.field(WILDCARD_KEY, orderedAlphaKey);
                jsonBuilder.field(DATE_OF_CREATION_KEY, company.getDateOfCreation());
                if (company.getDateOfCessation() != null) {
                    jsonBuilder.field(DATE_OF_CESSATION_KEY, company.getDateOfCessation());
                }
                jsonBuilder.field(FULL_ADDRESS_KEY, createFullAddress(registeredOfficeAddress));
                jsonBuilder.field(RECORD_TYPE_KEY, RECORD_TYPE_VALUE);
                jsonBuilder.field(SAME_AS_KEY, sameAsKey);
            jsonBuilder.endObject();
            jsonBuilder.startObject(ITEMS_KEY);
                jsonBuilder.startObject(ADDRESS_KEY);
                    buildAddressJSON(jsonBuilder, registeredOfficeAddress);
                jsonBuilder.endObject();
                jsonBuilder.field(COMPANY_NUMBER_KEY, company.getCompanyNumber());
                jsonBuilder.field(COMPANY_STATUS_KEY, company.getCompanyStatus());
                jsonBuilder.field(CORPORATE_NAME_KEY, company.getCompanyName());
                jsonBuilder.array(SIC_CODES_KEY, company.getSicCodes());
                jsonBuilder.field(DATE_OF_CREATION_KEY, company.getDateOfCreation());
                if (company.getDateOfCessation() != null) {
                    jsonBuilder.field(DATE_OF_CESSATION_KEY, company.getDateOfCessation());
                }
                jsonBuilder.field(FULL_ADDRESS_KEY, createFullAddress(registeredOfficeAddress));
                jsonBuilder.field(SAME_AS_KEY, sameAsKey);
                jsonBuilder.field(WILDCARD_KEY, orderedAlphaKey);
                jsonBuilder.field(RECORD_TYPE_KEY, RECORD_TYPE_VALUE);
            jsonBuilder.endObject();
            jsonBuilder.field(KIND_KEY, KIND_VALUE);
            jsonBuilder.startObject(LINKS_KEY);
                jsonBuilder.field(SELF_KEY, links.get(SELF_KEY));
            jsonBuilder.endObject();
            jsonBuilder.field(SORT_KEY, orderedAlphaKey + "0");
        jsonBuilder.endObject();

        return jsonBuilder;
    }

    private void buildAddressJSON(XContentBuilder jsonBuilder, RegisteredOfficeAddressApi roa) throws IOException {
        jsonBuilder.field(PREMISES_KEY, roa.getPremises());
        jsonBuilder.field(ADDRESS_LINE_1_KEY, roa.getAddressLine1());
        jsonBuilder.field(ADDRESS_LINE_2_KEY, roa.getAddressLine2());
        jsonBuilder.field(POSTAL_CODE_KEY, roa.getPostalCode());
        jsonBuilder.field(LOCALITY_KEY, roa.getLocality());
        jsonBuilder.field(REGION_KEY, roa.getRegion());
        jsonBuilder.field(COUNTRY_KEY, roa.getCountry());
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

        if (!fullAddress.isEmpty() && fullAddress.charAt(fullAddress.length() - 2) == ',') {
            fullAddress = fullAddress.substring(0, fullAddress.length() - 2);
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


