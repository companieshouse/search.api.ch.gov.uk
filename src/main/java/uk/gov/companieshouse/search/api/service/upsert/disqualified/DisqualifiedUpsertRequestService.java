package uk.gov.companieshouse.search.api.service.upsert.disqualified;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.elasticsearch.DisqualifiedSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISQUALIFIED_SEARCH_INDEX;

@Service
public class DisqualifiedUpsertRequestService {

    @Autowired
    private DisqualifiedSearchUpsertRequest disqualifiedSearchUpsertRequest;

    private static final String INDEX = "DISQUALIFIED_SEARCH_INDEX";

    /**
     * If document already exists attempt to upsert the document
     * Note: Uses a deprecated UpdateRequest method for ES6 index
     * @param officer - Officer Disqualification sent over in REST call to be added/updated
     *
     * @return {@link UpdateRequest}
     * @throws UpsertException
     */
    public UpdateRequest createUpdateRequest(OfficerDisqualification officer, String officerId) throws UpsertException {

        Map<String, Object> logMap = setUpUpsertLogging(officer.getItems().get(0));
        try {

            UpdateRequest request =  new UpdateRequest("primary_search", "primary_search", officerId)
                    .docAsUpsert(true).doc(disqualifiedSearchUpsertRequest.buildRequest(officer), XContentType.JSON);

            LoggingUtils.getLogger().info("Attempt to upsert document if it does not exist", logMap);

            return request;

        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for officer" + e.getMessage(), logMap);
            throw new UpsertException("Unable to create update request");
        }
    }

    private Map<String, Object> setUpUpsertLogging(Item disqualification) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("officer name", disqualification.getForename() + " " + disqualification.getSurname());
        logMap.put(INDEX, DISQUALIFIED_SEARCH_INDEX);
        return logMap;
    }
}
