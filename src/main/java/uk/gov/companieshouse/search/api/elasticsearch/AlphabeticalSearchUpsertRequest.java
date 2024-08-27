package uk.gov.companieshouse.search.api.elasticsearch;

import java.io.IOException;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.json.JsonXContent;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

@Component
public class AlphabeticalSearchUpsertRequest {

    private static final String ID = "ID";
    private static final String COMPANY_TYPE = "company_type";
    private static final String ITEMS = "items";
    private static final String COMPANY_NUMBER = "company_number";
    private static final String COMPANY_STATUS = "company_status";
    private static final String CORPORATE_NAME = "corporate_name";
    private static final String RECORD_TYPE = "record_type";
    private static final String RECORD_TYPE_VALUE = "companies";
    private static final String LINKS = "links";
    private static final String SELF = "self";
    private static final String ORDERED_ALPHA_KEY = "ordered_alpha_key";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";

    public XContentBuilder buildRequest(
        CompanyProfileApi company,
        String orderedAlphaKey,
        String orderedAlphaKeyWithID) throws IOException {
        return JsonXContent.contentBuilder()
            .startObject()
            .field(ID, company.getCompanyNumber())
            .field(ORDERED_ALPHA_KEY_WITH_ID, orderedAlphaKeyWithID)
            .field(COMPANY_TYPE, company.getType())
            .startObject(ITEMS)
            .field(COMPANY_NUMBER, company.getCompanyNumber())
            .field(ORDERED_ALPHA_KEY, orderedAlphaKey)
            .field(COMPANY_STATUS, company.getCompanyStatus())
            .field(CORPORATE_NAME, company.getCompanyName())
            .field(RECORD_TYPE, RECORD_TYPE_VALUE)
            .endObject()
            .startObject(LINKS)
            .field(SELF, company.getLinks().get(SELF))
            .endObject()
            .endObject();
    }
}
