package uk.gov.companieshouse.search.api.service.delete.primary;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchType;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;

import java.io.IOException;
import java.util.Map;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@Service
public class PrimarySearchDeleteService {

    private final PrimarySearchRestClientService primarySearchRestClientService;

    private final PrimarySearchDeleteRequestService primarySearchDeleteRequestService;

    private final ConfiguredIndexNamesProvider indices;

    public PrimarySearchDeleteService(PrimarySearchRestClientService primarySearchRestClientService,
            PrimarySearchDeleteRequestService primarySearchDeleteRequestService,
        ConfiguredIndexNamesProvider indices) {
        this.primarySearchRestClientService = primarySearchRestClientService;
        this.primarySearchDeleteRequestService = primarySearchDeleteRequestService;
        this.indices = indices;
    }

    public ResponseObject deleteOfficer(SearchType searchType) {

        Map<String, Object> logMap =
            LoggingUtils.setUpPrimarySearchDeleteLogging(searchType.getOfficerId(), indices);
        DeleteRequest deleteRequest = primarySearchDeleteRequestService.createDeleteRequest(searchType);
        return deleteObject(deleteRequest, searchType.getOfficerId(), "officer",logMap);
    }

    public ResponseObject deleteCompanyByNumber(String companyNumber) throws IOException {

        Map<String, Object> logMap =
                LoggingUtils.setUpPrimarySearchCompanyDeleteLogging(companyNumber, indices);

        DeleteRequest deleteRequest = new DeleteRequest(indices.primary(),"primary_search",companyNumber);
        return deleteObject(deleteRequest,companyNumber,"company",logMap);
    }

    private ResponseObject deleteObject(DeleteRequest deleteRequest, String id, String entityType, Map<String, Object> logMap){
        DeleteResponse response;
        try {
            response = primarySearchRestClientService.delete(deleteRequest);
        } catch (IOException e) {
            getLogger().error(String
                    .format("IOException encountered when deleting %s [%s] from primary search index",
                            entityType, id),logMap);
            return new ResponseObject(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject(ResponseStatus.DELETE_REQUEST_ERROR);
        }
        if (response.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            getLogger().error(String.format("%s [%s] not found",
                    entityType, id),logMap);
            return new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
        } else {
            getLogger().info(String.format("Successfully deleted %s [%s] ",
                    entityType, id),logMap);
            return new ResponseObject(ResponseStatus.DOCUMENT_DELETED);
        }

    }
}
