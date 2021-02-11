package uk.gov.companieshouse.search.api.service.search;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchRequestService;

import java.io.IOException;

import static org.apache.lucene.search.TotalHits.Relation.EQUAL_TO;
import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlphabeticalSearchRequestServiceTest {

    @InjectMocks
    private SearchRequestService searchRequestService = new AlphabeticalSearchRequestService();

    @Mock
    private AlphaKeyService mockAlphaKeyService;

    @Mock
    private AlphabeticalSearchRequests mockAlphabeticalSearchRequests;

    private static final String CORPORATE_NAME = "corporateName";
    private static final String TOP_HIT = "TEST COMPANY";
    private static final String ORDERED_ALPHA_KEY = "orderedAlphaKey";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String REQUEST_ID = "requestId";

    @Test
    @DisplayName("Test search request returns results successfully with best match query")
    void testBestMatchSuccessful() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME))
            .thenReturn(createAlphaKeyResponse());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
            .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getAboveResultsResponse(REQUEST_ID,
            ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
            .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getDescendingResultsResponse(REQUEST_ID,
            ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
            .thenReturn(createSearchHits());

        SearchResults searchResults =
            searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals( TOP_HIT, searchResults.getTopHit());
        assertEquals(3, searchResults.getResults().size());
    }

    @Test
    @DisplayName("Test search request returns results successfully with starts with query")
    void testStartsWithSuccessful() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME))
            .thenReturn(createAlphaKeyResponse());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
            .thenReturn(createEmpySearchHits());

        when(mockAlphabeticalSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
            .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getAboveResultsResponse(REQUEST_ID,
            ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
            .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getDescendingResultsResponse(REQUEST_ID,
            ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
            .thenReturn(createSearchHits());

        SearchResults searchResults =
            searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(TOP_HIT, searchResults.getTopHit());
        assertEquals(3, searchResults.getResults().size());
    }

    @Test
    @DisplayName("Test search request returns results successfully with corporate name starts with query")
    void testCorporateNameStartsWithSuccessful() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME))
            .thenReturn(createAlphaKeyResponse());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
            .thenReturn(createEmpySearchHits());

        when(mockAlphabeticalSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
            .thenReturn(createEmpySearchHits());

        when(mockAlphabeticalSearchRequests.getCorporateNameStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
            .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getAboveResultsResponse(REQUEST_ID,
            ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
            .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getDescendingResultsResponse(REQUEST_ID,
            ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
            .thenReturn(createSearchHits());

        SearchResults searchResults =
            searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(TOP_HIT, searchResults.getTopHit());
        assertEquals(3, searchResults.getResults().size());
    }

    @Test
    @DisplayName("Test search request throws exception")
    void testThrowException() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME))
            .thenReturn(createAlphaKeyResponse());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
            .thenThrow(IOException.class);

        assertThrows(SearchException.class, () ->
            searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, REQUEST_ID));
    }

    private SearchHits createSearchHits() {
        BytesReference source = new BytesArray(
            "{" +
                "\"ID\": \"id\"," +
                "\"company_type\": \"companyType\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"items\" : {" +
                    "\"company_number\" : \"00000000\"," +
                    "\"company_status\" : \"active\"," +
                    "\"corporate_name\" : \"TEST COMPANY\"" +
                "}," +
                "\"links\" : {" +
                    "\"self\" : \"TEST\"" +
                "}" +
            "}");
        SearchHit hit = new SearchHit( 1 );
        hit.sourceRef( source );
        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits( new SearchHit[] { hit }, totalHits, 10 );
    }

    private SearchHits createEmpySearchHits() {
        TotalHits totalHits = new TotalHits(0, EQUAL_TO);
        return new SearchHits( new SearchHit[] {}, totalHits, 0 );
    }

    private AlphaKeyResponse createAlphaKeyResponse() {
        AlphaKeyResponse alphaKeyResponse = new AlphaKeyResponse();

        alphaKeyResponse.setOrderedAlphaKey("orderedAlphaKey");
        return alphaKeyResponse;
    }
}
