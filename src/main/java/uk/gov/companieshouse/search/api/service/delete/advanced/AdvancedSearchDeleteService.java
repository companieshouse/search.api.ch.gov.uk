package uk.gov.companieshouse.search.api.service.delete.advanced;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import java.io.IOException;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@Service
public class AdvancedSearchDeleteService {

    private final ConfiguredIndexNamesProvider indices;

    private final AdvancedSearchRestClientService advancedSearchRestClientService;

    @Autowired
    public AdvancedSearchDeleteService(ConfiguredIndexNamesProvider indices,
                                       AdvancedSearchRestClientService advancedSearchRestClientService){
        this.indices = indices;
        this.advancedSearchRestClientService = advancedSearchRestClientService;
    }

    public ResponseObject<String> deleteCompanyByNumber(String companyNumber){

        DeleteRequest deleteRequest = new DeleteRequest(indices.advanced(), companyNumber);

        DeleteResponse response;
        try{
            response = advancedSearchRestClientService.delete(deleteRequest);
        }catch (IOException e) {
            getLogger().error(String.format("IOException encountered when deleting Company Number [%s] from the advanced search index",
                    companyNumber));
            return new ResponseObject<>(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject<>(ResponseStatus.DELETE_REQUEST_ERROR);
        }

        if (response.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            getLogger().error(String.format("Company Number: [%s] not found in advanced search index",
                    companyNumber));
            return new ResponseObject<>(ResponseStatus.DELETE_NOT_FOUND);
        } else {
            getLogger().info(String.format("Successfully deleted Company Number [%s] from the advanced search index",
                    companyNumber));
            return new ResponseObject<>(ResponseStatus.DOCUMENT_DELETED);
        }

    }
}
