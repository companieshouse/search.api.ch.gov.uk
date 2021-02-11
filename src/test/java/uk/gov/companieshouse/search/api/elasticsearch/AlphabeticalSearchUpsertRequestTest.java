package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AlphabeticalSearchUpsertRequestTest {

    private static final String COMPANY_TYPE = "company_type";
    private static final String COMPANY_NUMBER = "12345";
    private static final String COMPANY_STATUS = "company_status";
    private static final String CORPORATE_NAME = "corporate_name";
    private static final String ORDERED_ALPHA_KEY_FIELD = "orderedAlphaKey";
    private static final String ORDERED_ALPHA_KEY_WITH_ID_FIELD = "orderedAlphaKey:12345";

    @Test
    @DisplayName("Test build request successful")
    void testBuildRequestReturnsXContentBuilder() throws Exception{

        AlphabeticalSearchUpsertRequest alphabeticalSearchUpsertRequest = new AlphabeticalSearchUpsertRequest();
        XContentBuilder xContentBuilder =
            alphabeticalSearchUpsertRequest.buildRequest(createCompany(), ORDERED_ALPHA_KEY_FIELD,
            ORDERED_ALPHA_KEY_WITH_ID_FIELD);

        assertNotNull(xContentBuilder);
    }

    private CompanyProfileApi createCompany() {
        CompanyProfileApi company = new CompanyProfileApi();
        company.setType(COMPANY_TYPE);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setCompanyName(CORPORATE_NAME);

        Map<String, String> links = new HashMap<>();
        links.put("self", "company/00000000");
        company.setLinks(links);

        return company;
    }
}
