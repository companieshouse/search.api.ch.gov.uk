package uk.gov.companieshouse.search.api.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.RegisteredOfficeAddressApi;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

class AdvancedSearchUpsertRequestTest {

    private static final String COMPANY_TYPE = "ltd";
    private static final String COMPANY_SUBTYPE = "test subtype";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String COMPANY_STATUS = "active";
    private static final String COMPANY_NAME = "test company ltd";
    private static final String[] SIC_CODES = {"12345"};
    private static final String ADDRESS_LINE_1 = "address line 1";
    private static final String ADDRESS_LINE_2 = "address line 2";
    private static final String POSTAL_CODE = "postal_code";
    private static final String LOCALITY = "locality";
    private static final LocalDate DATE_OF_CREATION = LocalDate.of(1983, 01, 01);
    private static final LocalDate DATE_OF_CESSATION = LocalDate.of(1993, 01, 01);

    private static final String ALPHA_KEY = "testcompany";

    @Test
    @DisplayName("Test build request is successful")
    void testBuildRequestReturnsXContentBuilder() throws Exception{

        AdvancedSearchUpsertRequest advancedSearchUpsertRequest = new AdvancedSearchUpsertRequest();
        XContentBuilder xContentBuilder =
            advancedSearchUpsertRequest.buildRequest(createCompany(true), ALPHA_KEY, ALPHA_KEY);

        assertNotNull(xContentBuilder);
    }

    @Test
    @DisplayName("Test build request is successful even when registered office address fields all empty")
    void testBuildRequestReturnsXContentBuilderWithEmptyRegisteredOfficeAddress() throws Exception{

        AdvancedSearchUpsertRequest advancedSearchUpsertRequest = new AdvancedSearchUpsertRequest();
        XContentBuilder xContentBuilder =
                advancedSearchUpsertRequest.buildRequest(createCompany(false), ALPHA_KEY, ALPHA_KEY);

        assertNotNull(xContentBuilder);
    }

    private CompanyProfileApi createCompany(final boolean populateRegisteredAddressFields) {
        CompanyProfileApi company = new CompanyProfileApi();
        company.setType(COMPANY_TYPE);
        company.setSubtype(COMPANY_SUBTYPE);
        company.setCompanyName(COMPANY_NAME);
        company.setSicCodes(SIC_CODES);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);

        RegisteredOfficeAddressApi registeredOfficeAddressApi = new RegisteredOfficeAddressApi();
        if (populateRegisteredAddressFields) {
            registeredOfficeAddressApi.setAddressLine1(ADDRESS_LINE_1);
            registeredOfficeAddressApi.setAddressLine2(ADDRESS_LINE_2);
            registeredOfficeAddressApi.setPostalCode(POSTAL_CODE);
            registeredOfficeAddressApi.setLocality(LOCALITY);
        }
        company.setRegisteredOfficeAddress(registeredOfficeAddressApi);

        company.setDateOfCreation(DATE_OF_CREATION);
        company.setDateOfCessation(DATE_OF_CESSATION);

        Map<String, String> links = new HashMap<>();
        links.put("self", "company/00000000");
        company.setLinks(links);

        return company;
    }
}
