package uk.gov.companieshouse.search.api.service.search.dissolved;

import static org.apache.lucene.search.TotalHits.Relation.EQUAL_TO;
import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.elasticsearch.DissolvedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchRequestService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class DissolvedSearchRequestServiceTest {

    @InjectMocks
    private DissolvedSearchRequestService dissolvedSearchRequestService;

    @Mock
    private AlphaKeyService mockAlphaKeyService;

    @Mock
    private DissolvedSearchRequests mockDissolvedSearchRequests;

    @Mock
    private ElasticSearchResponseMapper mockElasticSearchResponseMapper;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String COMPANY_STATUS = "dissolved";
    private static final String KIND = "searchresults#dissolved-company";
    private static final String LOCALITY = "locality";
    private static final String POSTCODE = "AB00 0 AB";
    private static final String PREVIOUS_COMPANY_NAME = "TEST COMPANY 2";
    private static final String ORDERED_ALPHA_KEY = "orderedAlphaKey";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String REQUEST_ID = "requestId";
    private static final String SEARCH_TYPE_BEST_MATCH = "best-match";
    private static final String SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH = "previous-name-dissolved";
    private static final String PREVIOUS_NAME_KIND = "search#previous-name-dissolved";
    private static final String BEST_MATCH_KIND = "search#dissolved";
    private static final String DISSOLVED_ALPHABETICAL_KIND = "search#alphabetical-dissolved";
    private static final Integer SIZE = 20;
    private static final Integer START_INDEX = 0;
    private static final String SEARCH_BEFORE_VALUE = "search_before:1234";
    private static final String SEARCH_AFTER_VALUE = "search_after:1234";
    private static final String DISSOLVED_ALPHABETICAL_FALLBACK_QUERY_LIMIT = "DISSOLVED_ALPHABETICAL_FALLBACK_QUERY_LIMIT";

    @Test
    @DisplayName("Test dissolved alphabetical search request returns results successfully with best match query")
    void testDissolvedAlphabeticalBestMatchSuccessful() throws Exception {

        SearchHits searchHits = createSearchHits(true, true, true, true);
        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID)).thenReturn(searchHits);

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, COMPANY_NAME,
                10)).thenReturn(searchHits);

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID,
                COMPANY_NAME, 9)).thenReturn(searchHits);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getSearchResults(COMPANY_NAME, null, null, SIZE, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
        assertEquals(DISSOLVED_ALPHABETICAL_KIND, dissolvedSearchResults.getKind());
    }
    
    @Test
    @DisplayName("Test dissolved alphabetical search request returns results successfully with best match query and a size of one")
    void testDissolvedAlphabeticalBestMatchSuccessfulWithSizeOne() throws Exception {

        SearchHits searchHits = createSearchHits(true, true, true, true);
        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID)).thenReturn(searchHits);

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getSearchResults(COMPANY_NAME, null, null, 1, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(1, dissolvedSearchResults.getItems().size());
        assertEquals(DISSOLVED_ALPHABETICAL_KIND, dissolvedSearchResults.getKind());
    }

    @Test
    @DisplayName("Test search request returns results successfully with best match query no previous names")
    void testDissolvedAlphabeticalBestMatchSuccessfulWithNoPreviousNames() throws Exception {

        SearchHits searchHits = createSearchHits(true, true, true, false);
        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID)).thenReturn(searchHits);

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, COMPANY_NAME,
                1)).thenReturn(searchHits);

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID,
                COMPANY_NAME, 1)).thenReturn(searchHits);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getSearchResults(COMPANY_NAME, null, null, 3, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
        assertEquals(DISSOLVED_ALPHABETICAL_KIND, dissolvedSearchResults.getKind());
    }

    @Test
    @DisplayName("Test search request returns results successfully with starts with query")
    void testDissolvedAlphabeticalStartsWithSuccessful() throws Exception {

        SearchHits searchHits = createSearchHits(true, true, true, true);
        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID)).thenReturn(searchHits);

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, COMPANY_NAME,
                10)).thenReturn(searchHits);

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID,
                COMPANY_NAME, 9)).thenReturn(searchHits);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getSearchResults(COMPANY_NAME, null, null, SIZE, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertNotNull(dissolvedSearchResults.getEtag());
        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
        assertEquals(DISSOLVED_ALPHABETICAL_KIND, dissolvedSearchResults.getKind());
    }



    @ParameterizedTest(name = "Search with locality={0}, postcode={1}, address={2}, corporateName={3}")
    @CsvSource({
            "true,  true,  true,  true",
            "true,  false, true,  true",
            "true,  true,  false, true",
            "false, true,  false, true"
    })
    void testCategoryNameStartsWithSuccessful(
            boolean locality,
            boolean postcode,
            boolean address,
            boolean categoryName
    ) throws Exception {

        SearchHits searchHits = createSearchHits(locality, postcode, address, categoryName);
        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createEmptySearchHits());

        when(mockDissolvedSearchRequests.getCorporateNameStartsWithResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(searchHits);

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);
        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, COMPANY_NAME, 10))
                .thenReturn(searchHits);

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, COMPANY_NAME, 9))
                .thenReturn(searchHits);

        // Act
        SearchResults<Company> results = dissolvedSearchRequestService
                .getSearchResults(COMPANY_NAME, null, null, SIZE, REQUEST_ID);

        // Assert
        assertNotNull(results);
        assertEquals(COMPANY_NAME, results.getTopHit().getCompanyName());
        assertEquals(3, results.getItems().size());
        assertEquals(DISSOLVED_ALPHABETICAL_KIND, results.getKind());
    }



    @Test
    @DisplayName("Test search request throws exception")
    void testThrowException() throws Exception {

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenThrow(IOException.class);

        assertThrows(SearchException.class,
                () -> dissolvedSearchRequestService.getSearchResults(COMPANY_NAME, null, null, null, REQUEST_ID));
    }

    @Test
    @DisplayName("Test best match search results successful")
    void testBestMatchSuccessful() throws Exception {

        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockDissolvedSearchRequests.getDissolved(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE))
                .thenReturn(createSearchHits(true, true, true, true));

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getBestMatchSearchResults(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE);

        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(BEST_MATCH_KIND, dissolvedSearchResults.getKind());
    }

    @Test
    @DisplayName("Test get best match search request throws exception")
    void testBestMatchThrowException() throws Exception {

        when(mockDissolvedSearchRequests.getDissolved(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE))
                .thenThrow(IOException.class);

        assertThrows(SearchException.class, () -> dissolvedSearchRequestService.getBestMatchSearchResults(COMPANY_NAME,
                REQUEST_ID, SEARCH_TYPE_BEST_MATCH, START_INDEX, SIZE));
    }

    @Test
    @DisplayName("Test previous names best match search results successful")
    void testPreviousNamesBestMatchSuccessful() throws Exception {

        List<Company> results = new ArrayList<>();
        results.add(createCompany());
        TopHit topHit = createTopHit();

        when(mockDissolvedSearchRequests.getDissolved(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH,
                START_INDEX, SIZE)).thenReturn(createSearchHits(true, true, true, true));

        when(mockElasticSearchResponseMapper.mapPreviousNames(any(SearchHits.class))).thenReturn(results);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(results.get(0))).thenReturn(topHit);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getPreviousNamesResults(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE);

        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(PREVIOUS_NAME_KIND, dissolvedSearchResults.getKind());
    }

    @Test
    @DisplayName("Test get previous names best match search request throws exception")
    void testPreviousNamesBestMatchThrowException() throws Exception {

        when(mockDissolvedSearchRequests.getDissolved(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH,
                START_INDEX, SIZE)).thenThrow(IOException.class);

        assertThrows(SearchException.class, () -> dissolvedSearchRequestService.getPreviousNamesResults(COMPANY_NAME,
                REQUEST_ID, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE));
    }

    @Test
    @DisplayName("Test get best match previous names search request throws exception")
    void testBestMatchPreviousNamesThrowException() throws Exception {

        when(mockDissolvedSearchRequests.getDissolved(COMPANY_NAME, REQUEST_ID, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH,
                START_INDEX, SIZE)).thenThrow(IOException.class);

        assertThrows(SearchException.class, () -> dissolvedSearchRequestService.getBestMatchSearchResults(COMPANY_NAME,
                REQUEST_ID, SEARCH_TYPE_PREVIOUS_NAME_BEST_MATCH, START_INDEX, SIZE));
    }

    @Test
    @DisplayName("Test peelbackSearchRequest successful")
    void testPeelbackSearchRequestSuccessful() throws Exception {

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID))
                .thenReturn(createSearchHits(true, true, true, true));
        doReturn(25).when(mockEnvironmentReader).getMandatoryInteger(DISSOLVED_ALPHABETICAL_FALLBACK_QUERY_LIMIT);

        SearchHits searchHits = dissolvedSearchRequestService.peelbackSearchRequest(createEmptySearchHits(),
                ORDERED_ALPHA_KEY, REQUEST_ID);

        assertEquals(1L, searchHits.getTotalHits().value);
    }

    @Test
    @DisplayName("Test search only searches above values when search_before specified")
    void testGetSearchResultsWithSearchBeforeValue() throws Exception {

        SearchHits searchHits = createSearchHits(true, true, true, true);
        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID)).thenReturn(searchHits);

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID, SEARCH_BEFORE_VALUE, COMPANY_NAME, SIZE))
                .thenReturn(searchHits);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getSearchResults(COMPANY_NAME, SEARCH_BEFORE_VALUE, null, SIZE, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(1, dissolvedSearchResults.getItems().size());
        assertEquals(DISSOLVED_ALPHABETICAL_KIND, dissolvedSearchResults.getKind());
    }

    @Test
    @DisplayName("Test search only searches below values when search_after specified")
    void testGetSearchResultsWithSearchAfterValue() throws Exception {

        SearchHits searchHits = createSearchHits(true, true, true, true);
        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID)).thenReturn(searchHits);

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID, SEARCH_AFTER_VALUE, COMPANY_NAME,
                SIZE)).thenReturn(searchHits);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getSearchResults(COMPANY_NAME, null, SEARCH_AFTER_VALUE, SIZE, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(1, dissolvedSearchResults.getItems().size());
        assertEquals(DISSOLVED_ALPHABETICAL_KIND, dissolvedSearchResults.getKind());
    }

    @Test
    @DisplayName("Test search uses default values when both search_before and search_after are provided")
    void testGetSearchResultsWhenBothSearchBeforeAndSearchAfterProvided() throws Exception {

        SearchHits searchHits = createSearchHits(true, true, true, true);
        Company topHitCompany = createCompany();
        TopHit topHit = createTopHit();

        when(mockAlphaKeyService.getAlphaKeyForCorporateName(COMPANY_NAME)).thenReturn(createAlphaKeyResponse());

        when(mockDissolvedSearchRequests.getBestMatchResponse(ORDERED_ALPHA_KEY, REQUEST_ID)).thenReturn(searchHits);

        when(mockElasticSearchResponseMapper.mapDissolvedResponse(any(SearchHit.class))).thenReturn(topHitCompany);

        when(mockElasticSearchResponseMapper.mapDissolvedTopHit(topHitCompany)).thenReturn(topHit);

        when(mockDissolvedSearchRequests.getAboveResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID, COMPANY_NAME,
                10)).thenReturn(searchHits);

        when(mockDissolvedSearchRequests.getDescendingResultsResponse(REQUEST_ID, ORDERED_ALPHA_KEY_WITH_ID,
                COMPANY_NAME, 9)).thenReturn(searchHits);

        SearchResults<Company> dissolvedSearchResults = dissolvedSearchRequestService
                .getSearchResults(COMPANY_NAME, SEARCH_BEFORE_VALUE, SEARCH_AFTER_VALUE, SIZE, REQUEST_ID);

        assertNotNull(dissolvedSearchResults);
        assertEquals(COMPANY_NAME, dissolvedSearchResults.getTopHit().getCompanyName());
        assertEquals(3, dissolvedSearchResults.getItems().size());
        assertEquals(DISSOLVED_ALPHABETICAL_KIND, dissolvedSearchResults.getKind());
    }

    private AlphaKeyResponse createAlphaKeyResponse() {
        AlphaKeyResponse alphaKeyResponse = new AlphaKeyResponse();

        alphaKeyResponse.setOrderedAlphaKey("orderedAlphaKey");
        return alphaKeyResponse;
    }

    private SearchHits createSearchHits(boolean includeAddress, boolean locality, boolean postCode,
            boolean includePreviousCompanyNames) {
        StringBuilder searchHits = new StringBuilder();
        searchHits.append("{" + "\"company_number\" : \"00000000\"," + "\"company_name\" : \"TEST COMPANY\","
                + "\"alpha_key\": \"alpha_key\"," + "\"ordered_alpha_key\": \"ordered_alpha_key\","
                + "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\","
                + "\"company_status\" : \"dissolved\",");
        if (includeAddress) {
            searchHits.append(populateAddress(locality, postCode));
        }
        if (includePreviousCompanyNames) {
            searchHits.append(populatePreviousCompanyNames());
        }
        searchHits.append("\"date_of_cessation\" : \"19990501\"," + "\"date_of_creation\" : \"19890501\"" + "}");
        BytesReference source = new BytesArray(searchHits.toString());
        SearchHit hit = new SearchHit(1);
        hit.sourceRef(source);
        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits(new SearchHit[] { hit }, totalHits, 10);
    }

    private SearchHits createEmptySearchHits() {
        TotalHits totalHits = new TotalHits(0, EQUAL_TO);
        return new SearchHits(new SearchHit[] {}, totalHits, 0);
    }

    private String populateAddress(boolean locality, boolean postCode) {
        StringBuilder address = new StringBuilder("\"address\" : {");
        if (locality) {
            address.append("\"locality\" : \"TEST\"");
            if (postCode)
                address.append(",");
        }
        if (postCode) {
            address.append("\"postal_code\" : \"TEST\"");
        }
        address.append("},");
        return address.toString();
    }

    private String populatePreviousCompanyNames() {
        return "\"previous_company_names\" : [" + "{" + "\"name\" : \"TEST COMPANY 2\","
                + "\"ordered_alpha_key\": \"ordered_alpha_key\"," + "\"effective_from\" : \"19890101\","
                + "\"ceased_on\" : \"19920510\"" + "}" + "],";
    }

    private Company createCompany() {
        Company company = new Company();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setKind(KIND);
        company.setDateOfCessation(LocalDate.parse("19990501", formatter));
        company.setDateOfCreation(LocalDate.parse("19890501", formatter));

        Address address = new Address();
        address.setPostalCode(POSTCODE);
        address.setLocality(LOCALITY);
        company.setRegisteredOfficeAddress(address);

        List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
        PreviousCompanyName previousCompanyName = new PreviousCompanyName();
        previousCompanyName.setName(PREVIOUS_COMPANY_NAME);
        previousCompanyName.setDateOfNameCessation(LocalDate.parse("19920510", formatter));
        previousCompanyName.setDateOfNameEffectiveness(LocalDate.parse("19890101", formatter));

        previousCompanyNames.add(previousCompanyName);
        company.setPreviousCompanyNames(previousCompanyNames);

        return company;
    }

    private TopHit createTopHit() {
        TopHit topHit = new TopHit();
        topHit.setCompanyName(COMPANY_NAME);
        topHit.setCompanyNumber(COMPANY_NUMBER);
        topHit.setCompanyStatus(COMPANY_STATUS);
        topHit.setKind(KIND);
        topHit.setDateOfCessation(LocalDate.parse("19990501", formatter));
        topHit.setDateOfCreation(LocalDate.parse("19890501", formatter));

        Address address = new Address();
        address.setPostalCode(POSTCODE);
        address.setLocality(LOCALITY);
        topHit.setRegisteredOfficeAddress(address);

        List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
        PreviousCompanyName previousCompanyName = new PreviousCompanyName();
        previousCompanyName.setName(PREVIOUS_COMPANY_NAME);
        previousCompanyName.setDateOfNameCessation(LocalDate.parse("19920510", formatter));
        previousCompanyName.setDateOfNameEffectiveness(LocalDate.parse("19890101", formatter));

        previousCompanyNames.add(previousCompanyName);
        topHit.setPreviousCompanyNames(previousCompanyNames);

        return topHit;
    }
}
