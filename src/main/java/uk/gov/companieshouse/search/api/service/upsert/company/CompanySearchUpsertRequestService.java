package uk.gov.companieshouse.search.api.service.upsert.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

public class CompanySearchUpsertRequestService {
    private static final String TYPE = "primary_search";
    private final ObjectMapper mapper;
    private final ConfiguredIndexNamesProvider indices;

    public CompanySearchUpsertRequestService(ObjectMapper mapper, ConfiguredIndexNamesProvider indices) {
        this.mapper = mapper;
        this.indices = indices;
    }

    public UpdateRequest createUpdateRequest(String companyNumber, Data profileData) throws UpsertException {

        Map<String, Object> logMap =
                LoggingUtils.setUpCompanySearchCompanyUpsertLogging(companyNumber, indices);

        try {
            String jsonString = mapper.writeValueAsString(profileData);

            return new UpdateRequest(indices.primary(), TYPE, companyNumber)
                    .docAsUpsert(true).doc(jsonString, XContentType.JSON);
        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for company profile" + e.getMessage(), logMap);
            throw new UpsertException("Unable to create update request");
        }
    }
}
