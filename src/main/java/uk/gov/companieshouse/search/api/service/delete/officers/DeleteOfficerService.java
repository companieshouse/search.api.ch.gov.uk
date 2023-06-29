package uk.gov.companieshouse.search.api.service.delete.officers;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;

import java.io.IOException;
import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@Service
public class DeleteOfficerService {

    @Autowired
    private PrimarySearchRestClientService primarySearchRestClientService;

    @Autowired
    private OfficerDeleteRequestService officerDeleteRequestService;

    public ResponseObject deleteOfficer(String officerId) {
        Map<String, Object> logMap = LoggingUtils.setUpOfficersDeleteLogging(officerId);
        DeleteRequest deleteRequest = officerDeleteRequestService.createDeleteRequest(officerId);

        DeleteResponse response;
        try {
            response = primarySearchRestClientService.delete(deleteRequest);
        } catch (IOException e) {
            getLogger().error("IOException encountered when deleting an officer from the primary search index", logMap);
            return new ResponseObject(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject(ResponseStatus.DELETE_REQUEST_ERROR);
        }

        if (response.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            getLogger().error("Document with id " + officerId + " not found", logMap);
            return new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
        } else {
            getLogger().info(String.format("Successfully deleted officer with id: [%s] from primary search index", officerId), logMap);
            return new ResponseObject(ResponseStatus.DOCUMENT_DELETED);
        }
    }
}
