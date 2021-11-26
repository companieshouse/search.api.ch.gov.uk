package uk.gov.companieshouse.search.api.service.upsert.advanced;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.elasticsearch.AdvancedSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdvancedUpsertRequestService {

    @Autowired
    private EnvironmentReader environmentReader;

    @Autowired
    private AdvancedSearchUpsertRequest advancedSearchUpsertRequest;

    private static final String INDEX = "ADVANCED_SEARCH_INDEX";

    /**
     * If document already exists attempt to upsert the document
     * @param company - Company sent over in REST call to be added/updated
     *
     * @return {@link UpdateRequest}
     * @throws UpsertException
     */
    public UpdateRequest createUpdateRequest(CompanyProfileApi company, String orderedAlphaKey,
                                             String sameAsKey) throws UpsertException {

        Map<String, Object> logMap = setUpUpsertLogging(company);
        try {
            LoggingUtils.getLogger().info("Attempt to upsert document if it does not exist", logMap);

            return new UpdateRequest(environmentReader.getMandatoryString(INDEX), company.getCompanyNumber())
                .docAsUpsert(true)
                .doc(advancedSearchUpsertRequest.buildRequest(company, orderedAlphaKey, sameAsKey));
        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for company", logMap);
            throw new UpsertException("Unable to create update request");
        }
    }

    private Map<String, Object> setUpUpsertLogging(CompanyProfileApi company) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LoggingUtils.COMPANY_NAME, company.getCompanyName());
        logMap.put(LoggingUtils.COMPANY_NUMBER, company.getCompanyNumber());
        logMap.put(LoggingUtils.INDEX, LoggingUtils.ADVANCED_SEARCH_INDEX);
        return logMap;
    }
}
