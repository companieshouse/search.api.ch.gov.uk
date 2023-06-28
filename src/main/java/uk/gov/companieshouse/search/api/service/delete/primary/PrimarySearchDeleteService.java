package uk.gov.companieshouse.search.api.service.delete.primary;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchType;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;

import java.io.IOException;
import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@Service
public class PrimarySearchDeleteService {

    @Autowired
    private PrimarySearchRestClientService primarySearchRestClientService;

    @Autowired
    private PrimarySearchDeleteRequestService primarySearchDeleteRequestService;

    public ResponseObject deleteOfficer(SearchType searchType) {

        Map<String, Object> logMap = LoggingUtils.setUpPrimarySearchDeleteLogging(searchType.getOfficerId());
        DeleteRequest deleteRequest = primarySearchDeleteRequestService.createDeleteRequest(searchType);

        DeleteResponse response;
        try {
            response = primarySearchRestClientService.delete(deleteRequest);
        } catch (IOException e) {
            getLogger().error(String.format("IOException encountered when deleting an [%s] from the primary search index",
                    searchType.getPrimarySearchType()), logMap);
            return new ResponseObject(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject(ResponseStatus.DELETE_REQUEST_ERROR);
        }

        if (response.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            getLogger().error(String.format("Document with id: [%s] not found in primary search index",
                    searchType.getOfficerId()), logMap);
            return new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
        } else {
            getLogger().info(String.format("Successfully deleted an [%s] with id: [%s] from primary search index",
                    searchType.getPrimarySearchType(), searchType.getOfficerId()), logMap);
            return new ResponseObject(ResponseStatus.DOCUMENT_DELETED);
        }

    }
}
