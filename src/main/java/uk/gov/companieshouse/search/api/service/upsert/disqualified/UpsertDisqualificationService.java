package uk.gov.companieshouse.search.api.service.upsert.disqualified;

import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.DisqualifiedSearchRestClientService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISQUALIFIED_SEARCH_INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@Service
public class UpsertDisqualificationService {

    @Autowired
    private DisqualifiedSearchRestClientService disqualifiedSearchRestClientService;

    @Autowired
    private DisqualifiedUpsertRequestService disqualifiedUpsertRequestService;

    /**
     * Upserts a new document to disqualified search index for an officer.
     * If a document does not exist it is added.
     * If the document does exist it is updated.
     *
     * @param officer -  Officer Disqualification sent over in REST call to be added/updated
     * @return {@link ResponseObject}
     */
    public ResponseObject upsertNaturalDisqualified(OfficerDisqualification officer, String officerId) {
        Map<String, Object> logMap = setUpUpsertLogging(officer.getItems().get(0));
        getLogger().info("Upserting to disqualified index underway", logMap);

        UpdateRequest updateRequest;

        try {
            updateRequest = disqualifiedUpsertRequestService.createUpdateRequest(officer, officerId);
        } catch (UpsertException e) {
            getLogger().error("An error occured attempting upsert the document to disqualified search index", logMap);
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            disqualifiedSearchRestClientService.upsert(updateRequest);
        } catch (IOException e) {
            getLogger().error("IOException when upserting a officer to the disqualified search index", logMap);
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        getLogger().info("Upsert successful to disqualified search index", logMap);
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }

    private Map<String, Object> setUpUpsertLogging(Item disqualification) {
        Map<String, Object> logMap = new HashMap<>();
        if (disqualification.getCorporateName() != null && disqualification.getCorporateName().length() > 0) {
            logMap.put("officer name", disqualification.getCorporateName());
        } else {
            logMap.put("officer name", disqualification.getForename() + " " + disqualification.getSurname());
        }
        logMap.put(INDEX, DISQUALIFIED_SEARCH_INDEX);
        return logMap;
    }
}
