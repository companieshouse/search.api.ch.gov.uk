package uk.gov.companieshouse.search.api.service.search.alphabetical;

import static org.apache.lucene.search.TotalHits.Relation.EQUAL_TO;
import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchRequestService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class AlphabeticalSearchRequestServiceTest {

    @InjectMocks
    private AlphabeticalSearchRequestService searchRequestService;

    @Mock
    private AlphaKeyService mockAlphaKeyService;

    @Mock
    private AlphabeticalSearchRequests mockAlphabeticalSearchRequests;

    @Mock
    private ElasticSearchResponseMapper mockElasticSearchResponseMapper;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    private static final String CORPORATE_NAME = "corporateName";
    private static final String TOP_HIT = "TEST COMPANY";
    private static final String ORDERED_ALPHA_KEY = "orderedAlphaKey";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String REQUEST_ID = "requestId";
    private static final String SEARCH_BEFORE_VALUE = "search_before:1234";
    private static final String SEARCH_AFTER_VALUE = "search_after:1234";
    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String COMPANY_STATUS = "dissolved";
    private static final String COMPANY_TYPE = "ltd";
    private static final String COMPANY_PROFILE_LINK = "/company/00000000";
    private static final String KIND = "searchresults#dissolved-company";
    private static final String ALPHABETICAL_FALLBACK_QUERY_LIMIT = "ALPHABETICAL_FALLBACK_QUERY_LIMIT";

    @Test
    @DisplayName("Test search request returns results successfully with best match query")
    void testBestMatchSuccessful() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME)).thenReturn(createAlphaKeyResponse());

        Company company = createCompany();

        when(mockElasticSearchResponseMapper.mapAlphabeticalResponse(createSearchHits().getAt(0))).thenReturn(company);

        when(mockElasticSearchResponseMapper.mapAlphabeticalTopHit(company)).thenReturn(createTopHit());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT,
                10)).thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT,
                9)).thenReturn(createSearchHits());

        SearchResults<Company> searchResults =
            searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, 20, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals( TOP_HIT, searchResults.getTopHit().getCompanyName());
        assertEquals(3, searchResults.getItems().size());
    }
    
    @Test
    @DisplayName("Test search request returns results successfully with best match query and a size of 1")
    void testBestMatchSuccessfulWithSizeOne() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME)).thenReturn(createAlphaKeyResponse());

        Company company = createCompany();

        when(mockElasticSearchResponseMapper.mapAlphabeticalResponse(createSearchHits().getAt(0))).thenReturn(company);

        when(mockElasticSearchResponseMapper.mapAlphabeticalTopHit(company)).thenReturn(createTopHit());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits());

        SearchResults<Company> searchResults =
            searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, 1, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals( TOP_HIT, searchResults.getTopHit().getCompanyName());
        assertEquals(1, searchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search request returns results successfully with starts with query")
    void testStartsWithSuccessful() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME)).thenReturn(createAlphaKeyResponse());

        Company company = createCompany();

        when(mockElasticSearchResponseMapper.mapAlphabeticalResponse(createSearchHits().getAt(0))).thenReturn(company);

        when(mockElasticSearchResponseMapper.mapAlphabeticalTopHit(company)).thenReturn(createTopHit());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockAlphabeticalSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT,
                10)).thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT,
                9)).thenReturn(createSearchHits());

        SearchResults<Company> searchResults =
            searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, 20, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(TOP_HIT, searchResults.getTopHit().getCompanyName());
        assertEquals(3, searchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search request returns results successfully with corporate name starts with query")
    void testCorporateNameStartsWithSuccessful() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME)).thenReturn(createAlphaKeyResponse());

        Company company = createCompany();

        when(mockElasticSearchResponseMapper.mapAlphabeticalResponse(createSearchHits().getAt(0))).thenReturn(company);

        when(mockElasticSearchResponseMapper.mapAlphabeticalTopHit(company)).thenReturn(createTopHit());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockAlphabeticalSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockAlphabeticalSearchRequests.getCorporateNameStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT,
                5)).thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT,
                5)).thenReturn(createSearchHits());

        SearchResults<Company> searchResults =
            searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, 11, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(TOP_HIT, searchResults.getTopHit().getCompanyName());
        assertEquals(3, searchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search request throws exception")
    void testThrowException() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenThrow(IOException.class);

        assertThrows(SearchException.class,
                () -> searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME, null, null, null, REQUEST_ID));
    }

    @Test
    @DisplayName("Test peelbackSearchRequest successful")
    void testPeelbackSearchRequestSuccessful() throws Exception {

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits());
        doReturn(25).when(mockEnvironmentReader).getMandatoryInteger(ALPHABETICAL_FALLBACK_QUERY_LIMIT);

        SearchHits searchHits = searchRequestService.peelbackSearchRequest(createEmptySearchHits(), ORDERED_ALPHA_KEY,
                REQUEST_ID);

        assertEquals(1L, searchHits.getTotalHits().value);
    }

    @Test
    @DisplayName("Test search request returns results successfully when search_before is not null")
    void testSearchUsinfSearchBefore() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME)).thenReturn(createAlphaKeyResponse());

        Company company = createCompany();

        when(mockElasticSearchResponseMapper.mapAlphabeticalResponse(createSearchHits().getAt(0))).thenReturn(company);

        when(mockElasticSearchResponseMapper.mapAlphabeticalTopHit(company)).thenReturn(createTopHit());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getAboveResultsResponse(REQUEST_ID, SEARCH_BEFORE_VALUE, TOP_HIT, null))
                .thenReturn(createSearchHits());

        SearchResults<Company> searchResults = searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME,
                SEARCH_BEFORE_VALUE, null, null, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(TOP_HIT, searchResults.getTopHit().getCompanyName());
        assertEquals(1, searchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search request returns results successfully when search_after is not null")
    void testSearchUsinfSearchAfter() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME)).thenReturn(createAlphaKeyResponse());

        Company company = createCompany();

        when(mockElasticSearchResponseMapper.mapAlphabeticalResponse(createSearchHits().getAt(0))).thenReturn(company);

        when(mockElasticSearchResponseMapper.mapAlphabeticalTopHit(company)).thenReturn(createTopHit());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getDescendingResultsResponse(REQUEST_ID, SEARCH_AFTER_VALUE, TOP_HIT, null))
                .thenReturn(createSearchHits());

        SearchResults<Company> searchResults = searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME,
                null, SEARCH_AFTER_VALUE, null, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(TOP_HIT, searchResults.getTopHit().getCompanyName());
        assertEquals(1, searchResults.getItems().size());
    }

    @Test
    @DisplayName("Test search uses default behaviour when both search_before and search after are provided")
    void testSearchWhenBothSearchBeforeAndSearchAfterProvided() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME)).thenReturn(createAlphaKeyResponse());

        Company company = createCompany();

        when(mockElasticSearchResponseMapper.mapAlphabeticalResponse(createSearchHits().getAt(0))).thenReturn(company);

        when(mockElasticSearchResponseMapper.mapAlphabeticalTopHit(company)).thenReturn(createTopHit());

        when(mockAlphabeticalSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT,
                10)).thenReturn(createSearchHits());

        when(mockAlphabeticalSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, TOP_HIT,
                9)).thenReturn(createSearchHits());

        SearchResults<Company> searchResults = searchRequestService.getAlphabeticalSearchResults(CORPORATE_NAME,
                SEARCH_BEFORE_VALUE, SEARCH_AFTER_VALUE, 20, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(TOP_HIT, searchResults.getTopHit().getCompanyName());
        assertEquals(3, searchResults.getItems().size());
    }

    private SearchHits createSearchHits() {
        BytesReference source = new BytesArray("{" + "\"ID\": \"id\"," + "\"company_type\": \"ltd\","
                + "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," + "\"items\" : {"
                + "\"company_number\" : \"00000000\"," + "\"company_status\" : \"active\","
                + "\"corporate_name\" : \"TEST COMPANY\"" + "}," + "\"links\" : {" + "\"self\" : \"/company/00000000\"" + "}" + "}");
        SearchHit hit = new SearchHit(1);
        hit.sourceRef(source);
        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits(new SearchHit[] { hit }, totalHits, 10);
    }

    private SearchHits createEmptySearchHits() {
        TotalHits totalHits = new TotalHits(0, EQUAL_TO);
        return new SearchHits(new SearchHit[] {}, totalHits, 0);
    }

    private AlphaKeyResponse createAlphaKeyResponse() {
        AlphaKeyResponse alphaKeyResponse = new AlphaKeyResponse();

        alphaKeyResponse.setOrderedAlphaKey("orderedAlphaKey");
        return alphaKeyResponse;
    }

    private Company createCompany() {
        Company company = new Company();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setCompanyType(COMPANY_TYPE);
        company.setOrderedAlphaKeyWithId(ORDERED_ALPHA_KEY_WITH_ID);
        company.setKind(KIND);

        Links links = new Links();
        links.setCompanyProfile(COMPANY_PROFILE_LINK);
        company.setLinks(links);

        return company;
    }

    private TopHit createTopHit() {
        TopHit topHit = new TopHit();
        topHit.setCompanyName(COMPANY_NAME);
        topHit.setCompanyNumber(COMPANY_NUMBER);
        topHit.setCompanyStatus(COMPANY_STATUS);
        topHit.setCompanyType(COMPANY_TYPE);
        topHit.setOrderedAlphaKeyWithId(ORDERED_ALPHA_KEY_WITH_ID);
        topHit.setKind(KIND);

        Links links = new Links();
        links.setCompanyProfile(COMPANY_PROFILE_LINK);
        topHit.setLinks(links);

        return topHit;
    }
}
