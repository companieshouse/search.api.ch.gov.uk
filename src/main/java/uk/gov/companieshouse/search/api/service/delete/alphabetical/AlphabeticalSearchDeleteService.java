package uk.gov.companieshouse.search.api.service.delete.alphabetical;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;

import java.io.IOException;
import java.util.Map;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@Service
public class AlphabeticalSearchDeleteService {

    private final AlphabeticalSearchRestClientService alphabeticalSearchRestClientService;

    private final ConfiguredIndexNamesProvider indices;

    public AlphabeticalSearchDeleteService(AlphabeticalSearchRestClientService alphabeticalSearchRestClientService,
                                           ConfiguredIndexNamesProvider indices) {
        this.alphabeticalSearchRestClientService = alphabeticalSearchRestClientService;
        this.indices = indices;
    }

    public ResponseObject deleteCompany(String companyNumber) {

        Map<String, Object> logMap =
                LoggingUtils.setUpAlphabeticalSearchDeleteLogging(companyNumber, indices);

        DeleteRequest deleteRequest = new DeleteRequest(indices.alphabetical(), companyNumber);

        DeleteResponse response;
        try {
            response = alphabeticalSearchRestClientService.delete(deleteRequest);
        } catch (IOException e) {
            getLogger().error(String.format("IOException encountered when deleting [%s] from the alphabetical search index",
                    companyNumber), logMap);
            return new ResponseObject(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject(ResponseStatus.DELETE_REQUEST_ERROR);
        }

        if (response.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            getLogger().error(String.format("Document with id: [%s] not found in alphabetical search index",
                    companyNumber), logMap);
            return new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
        } else {
            getLogger().info(String.format("Successfully deleted [%s] from alphabetical search index",
                    companyNumber), logMap);
            return new ResponseObject(ResponseStatus.DOCUMENT_DELETED);
        }
    }


}