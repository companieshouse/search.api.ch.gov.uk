package uk.gov.companieshouse.search.api.service.search.dissolved;


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
import uk.gov.companieshouse.search.api.elasticsearch.DissolvedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchRequestService;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchRequestService;

import java.io.IOException;

import static org.apache.lucene.search.TotalHits.Relation.EQUAL_TO;
import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DissolvedSearchRequestServiceTest {

    @InjectMocks
    private DissolvedSearchRequestService dissolvedSearchRequestService = new DissolvedSearchRequestService();

    @Mock
    private AlphaKeyService mockAlphaKeyService;

    @Mock
    private DissolvedSearchRequests mockDissolvedSearchRequests;

    private static final String COMPANY_NAME = "companyName";
    private static final String TOP_HIT = "TEST COMPANY";
    private static final String ORDERED_ALPHA_KEY = "orderedAlphaKey";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String REQUEST_ID = "requestId";

    @Test
    @DisplayName("Test search request returns results successfully with best match query")
    void testBestMatchSuccessful() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME))
                .thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits(true, true, true));

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, true));

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, true));

        DissolvedSearchResults dissolvedSearchResults =
                dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals( TOP_HIT, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search request returns results successfully with starts with query")
    void testStartsWithSuccessful() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME))
                .thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits(true, true, true));

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, true));

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, true));

        DissolvedSearchResults dissolvedSearchResults =
                dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(TOP_HIT, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search request returns results successfully with corporate name starts with query")
    void testCorporateNameStartsWithSuccessful() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME))
                .thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getCorporateNameStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits(true, true, true));

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, true));

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, true));

        DissolvedSearchResults dissolvedSearchResults =
                dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(TOP_HIT, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search request returns results successfully with no results found fallback query")
    void testNoResultsFoundFallbackSuccessful() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME))
                .thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getCorporateNameStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.noResultsFallbackQuery(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits(true, true, true));

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, true));

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, true));

        DissolvedSearchResults dissolvedSearchResults =
                dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(TOP_HIT, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
    }
    
    @Test
    @DisplayName("Test search request returns results successfully with corporate name starts with query when locality missing")
    void testCorporateNameStartsWithSuccessfulMissingLocality() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME))
                .thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getCorporateNameStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits(true, false, true));

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, false, true));

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, false, true));

        DissolvedSearchResults dissolvedSearchResults =
                dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(TOP_HIT, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
    }
    
    @Test
    @DisplayName("Test search request returns results successfully with corporate name starts with query when post code missing")
    void testCorporateNameStartsWithSuccessfulMissingPostCode() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME))
                .thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getCorporateNameStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits(true, true, false));

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, false));

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(true, true, false));

        DissolvedSearchResults dissolvedSearchResults =
                dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(TOP_HIT, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
    }
    
    @Test
    @DisplayName("Test search request returns results successfully with corporate name starts with query when address missing")
    void testCorporateNameStartsWithSuccessfulMissingAddress() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME))
                .thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getCorporateNameStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits(false, true, false));

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(false, true, false));

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID,
                ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT))
                .thenReturn(createSearchHits(false, true, false));

        DissolvedSearchResults dissolvedSearchResults =
                dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(TOP_HIT, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search request throws exception")
    void testThrowException() throws Exception{

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME))
                .thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenThrow(IOException.class);

        assertThrows(SearchException.class, () ->
                dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID));
    }


    private AlphaKeyResponse createAlphaKeyResponse() {
        AlphaKeyResponse alphaKeyResponse = new AlphaKeyResponse();

        alphaKeyResponse.setOrderedAlphaKey("orderedAlphaKey");
        return alphaKeyResponse;
    }

    private SearchHits createSearchHits(boolean includeAddress, boolean locality, boolean postCode) {
        StringBuilder searchHits = new StringBuilder();
        searchHits.append(
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\",");
        if(includeAddress) {
            populateAddress(locality, postCode);
        }
        searchHits.append(
                "\"date_of_cessation\" : \"01-05-1999\"," +
                "\"date_of_creation\" : \"01-05-1989\"," +
                "\"previous_company_names\" : [" +
                    "{" +
                        "\"name\" : \"TEST COMPANY 2\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"effective_from\" : \"01-01-1989\"," +
                        "\"ceased_on\" : \"10-05-1992\"" +
                    "}" +
                  "]" +   
            "}");
        BytesReference source = new BytesArray(searchHits.toString());
        SearchHit hit = new SearchHit( 1 );
        hit.sourceRef( source );
        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits( new SearchHit[] { hit }, totalHits, 10 );
    }

    private SearchHits createEmptySearchHits() {
        TotalHits totalHits = new TotalHits(0, EQUAL_TO);
        return new SearchHits( new SearchHit[] {}, totalHits, 0 );
    }
    
    private String populateAddress(boolean locality, boolean postCode) {
        StringBuilder address = new StringBuilder("\"address\" : {");
        if(locality) {
            address.append("\"locality\" : \"TEST\"");
            if(postCode)
                address.append(",");
        }
        if(postCode) {
            address.append("\"postal_code\" : \"TEST\"");
        }
        address.append("},");
        return address.toString();
    }

}
