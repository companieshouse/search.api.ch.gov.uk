package uk.gov.companieshouse.search.api.service.upsert.advanced;

import java.io.IOException;
import java.util.Map;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.elasticsearch.AdvancedSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class AdvancedUpsertRequestService {

    private final AdvancedSearchUpsertRequest advancedSearchUpsertRequest;
    private final ConfiguredIndexNamesProvider indices;

    public AdvancedUpsertRequestService(
        AdvancedSearchUpsertRequest advancedSearchUpsertRequest,
        ConfiguredIndexNamesProvider indices) {
        this.advancedSearchUpsertRequest = advancedSearchUpsertRequest;
        this.indices = indices;
    }

    /**
     * If document already exists attempt to upsert the document
     * @param company - Company sent over in REST call to be added/updated
     *
     * @return {@link UpdateRequest}
     * @throws UpsertException
     */
    public UpdateRequest createUpdateRequest(CompanyProfileApi company, String orderedAlphaKey,
                                             String sameAsKey) throws UpsertException {
        Map<String, Object> logMap = new DataMap.Builder()
                .companyName(company.getCompanyName())
                .companyNumber(company.getCompanyNumber())
                .indexName(indices.advanced())
                .build().getLogMap();
        try {
            LoggingUtils.getLogger().info("Attempt to upsert document if it does not exist", logMap);

            return new UpdateRequest(indices.advanced(), company.getCompanyNumber())
                .docAsUpsert(true)
                .doc(advancedSearchUpsertRequest.buildRequest(company, orderedAlphaKey, sameAsKey));
        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for company", logMap);
            throw new UpsertException("Unable to create update request");
        }
    }
}
