package uk.gov.companieshouse.search.api.service.upsert.disqualified;

import uk.gov.companieshouse.api.disqualification.Item;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.elasticsearch.DisqualifiedSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

import java.io.IOException;
import java.util.Map;

import javax.naming.ServiceUnavailableException;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class DisqualifiedUpsertRequestService {

    private static final String TYPE = "primary_search";
    private final DisqualifiedSearchUpsertRequest disqualifiedSearchUpsertRequest;
    private final AlphaKeyService alphaKeyService;
    private final ConfiguredIndexNamesProvider indices;

    public DisqualifiedUpsertRequestService(DisqualifiedSearchUpsertRequest disqualifiedSearchUpsertRequest,
        AlphaKeyService alphaKeyService,
        ConfiguredIndexNamesProvider indices) {
        this.disqualifiedSearchUpsertRequest = disqualifiedSearchUpsertRequest;
        this.alphaKeyService = alphaKeyService;
        this.indices = indices;
    }

    /**
     * If document already exists attempt to upsert the document
     * Note: Uses a deprecated UpdateRequest method for ES6 index
     * @param officer - Officer Disqualification sent over in REST call to be added/updated
     *
     * @return {@link UpdateRequest}
     * @throws UpsertException
     * @throws ServiceUnavailableException
     */
    public UpdateRequest createUpdateRequest(OfficerDisqualification officer, String officerId) throws UpsertException, ServiceUnavailableException {

        Map<String, Object> logMap =
            LoggingUtils.setUpDisqualificationUpsertLogging(officer.getItems().get(0), indices);

        if (officer.getSortKey() == null || officer.getSortKey().equals("")) {
            setKeyValues(officer, logMap);
        }

        try {
            UpdateRequest request = new UpdateRequest(indices.primary(), TYPE, officerId)
                    .docAsUpsert(true).doc(disqualifiedSearchUpsertRequest.buildRequest(officer), XContentType.JSON);

            LoggingUtils.getLogger().info("Attempt to upsert document if it does not exist", logMap);

            return request;

        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for officer" + e.getMessage(), logMap);
            throw new UpsertException("Unable to create update request");
        }
    }

    private void setKeyValues(OfficerDisqualification officer, Map<String, Object> logMap) throws ServiceUnavailableException {
        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(officer.getItems().get(0)
                .getCorporateName());
        if (alphaKeyResponse != null) {
            String orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey() + "1";
            logMap.put(LoggingUtils.ORDERED_ALPHAKEY, orderedAlphaKey);
            officer.setSortKey(orderedAlphaKey);
            for (Item item: officer.getItems()) {
                item.setWildcardKey(orderedAlphaKey);
            }
        } else {
            throw new ServiceUnavailableException("Unable to create ordered alpha key");
        }
    }
}
