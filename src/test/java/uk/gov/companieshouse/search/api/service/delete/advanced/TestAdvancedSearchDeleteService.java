package uk.gov.companieshouse.search.api.service.delete.advanced;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.rest.RestStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestAdvancedSearchDeleteService {
    @Mock
    private ConfiguredIndexNamesProvider indices;

    @Mock
    private AdvancedSearchRestClientService advancedSearchRestClientService;

    @InjectMocks
    private AdvancedSearchDeleteService deleteService;

    private static final String INDEX = "advanced_search";

    @Test
    void testDeleteCompanyByNumber_Success() throws IOException {
        String companyNumber = "12345678";
        when(indices.advanced()).thenReturn("advanced_search");

        // Stubbing the delete method to return a mock DeleteResponse
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, true);

        when(advancedSearchRestClientService.delete(any(DeleteRequest.class))).thenReturn(deleteResponse);

        ResponseObject response = deleteService.deleteCompanyByNumber(companyNumber);

        assertEquals(ResponseStatus.DOCUMENT_DELETED, response.getStatus());
    }

    @Test
    void testDeleteCompanyByNumber_IOException() throws IOException {
        String companyNumber = "12345678";
        when(advancedSearchRestClientService.delete(any(DeleteRequest.class))).thenThrow(IOException.class);

        ResponseObject response = deleteService.deleteCompanyByNumber(companyNumber);

        assertEquals(ResponseStatus.SERVICE_UNAVAILABLE, response.getStatus());
    }

    @Test
    void testDeleteCompanyByNumber_ElasticsearchException() throws IOException {
        String companyNumber = "12345678";
        when(advancedSearchRestClientService.delete(any(DeleteRequest.class))).thenThrow(ElasticsearchException.class);

        ResponseObject response = deleteService.deleteCompanyByNumber(companyNumber);

        assertEquals(ResponseStatus.DELETE_REQUEST_ERROR, response.getStatus());
    }

    @Test
    void testDeleteCompanyByNumber_NotFound() throws IOException {
        String companyNumber = "12345678";

        // Stubbing the delete method to return a mock DeleteResponse
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, false);
        when(advancedSearchRestClientService.delete(any(DeleteRequest.class))).thenReturn(deleteResponse);

        ResponseObject response = deleteService.deleteCompanyByNumber(companyNumber);

        assertEquals(ResponseStatus.DELETE_NOT_FOUND, response.getStatus());
    }
}
