package uk.gov.companieshouse.search.api.service.delete.alphabetical;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.index.shard.ShardId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlphabeticalSearchDeleteServiceTest {

    private static final String INDEX = "alphabetical_search";

    private static final String TEST_COMPANY_NUMBER = "00002500";

    @Mock
    AlphabeticalSearchRestClientService alphabeticalSearchRestClientService;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    @InjectMocks
    AlphabeticalSearchDeleteService service;

    @Test
    void deletesCompany() throws IOException {

        when(indices.alphabetical()).thenReturn("alphabetical_search");
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, true);

        when(alphabeticalSearchRestClientService.delete(any(DeleteRequest.class))).thenReturn(deleteResponse);

        ResponseObject response = service.deleteCompany(TEST_COMPANY_NUMBER);

        assertEquals(ResponseStatus.DOCUMENT_DELETED, response.getStatus());

    }

    @Test
    void returnsDeleteNotFoundWhenCompanyDoesNotExist() throws IOException {
        DeleteResponse deleteResponse = new DeleteResponse(
                new ShardId(INDEX, INDEX, 1), INDEX, "1", 1, 1, 1, false);

        when(alphabeticalSearchRestClientService.delete(any(DeleteRequest.class))).thenReturn(deleteResponse);

        ResponseObject response = service.deleteCompany(TEST_COMPANY_NUMBER);

        assertEquals(ResponseStatus.DELETE_NOT_FOUND, response.getStatus());
    }

    @Test
    void returnsServiceUnavailableOnIOException() throws IOException {

        when(alphabeticalSearchRestClientService.delete(any(DeleteRequest.class))).thenThrow(IOException.class);

        ResponseObject response = service.deleteCompany(TEST_COMPANY_NUMBER);

        assertEquals(ResponseStatus.SERVICE_UNAVAILABLE, response.getStatus());
    }

    @Test
    void returnsUpdateErrorOnElasticSearchException() throws Exception {

        when(alphabeticalSearchRestClientService.delete(any(DeleteRequest.class))).thenThrow(ElasticsearchException.class);

        ResponseObject response = service.deleteCompany(TEST_COMPANY_NUMBER);

        assertEquals(ResponseStatus.DELETE_REQUEST_ERROR, response.getStatus());
    }

}
