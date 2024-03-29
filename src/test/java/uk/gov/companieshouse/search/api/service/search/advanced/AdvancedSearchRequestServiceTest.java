package uk.gov.companieshouse.search.api.service.search.advanced;

import static org.apache.lucene.search.TotalHits.Relation.EQUAL_TO;
import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
import uk.gov.companieshouse.search.api.elasticsearch.AdvancedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchRequestService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class AdvancedSearchRequestServiceTest {

    @Mock
    private AdvancedSearchRequests mockAdvancedSearchRequests;

    @Mock
    private ElasticSearchResponseMapper mockElasticSearchResponseMapper;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    @InjectMocks
    private AdvancedSearchRequestService searchRequestService;

    private static final String COMPANY_NAME = "test company";
    private static final String REQUEST_ID = "123456789";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String COMPANY_STATUS = "dissolved";
    private static final String COMPANY_TYPE = "ltd";
    private static final String COMPANY_PROFILE_LINK = "/company/00000000";
    private static final String KIND = "searchresults#advanced-search-company";
    private static final String SIC_CODES = "99960";
    private static final List<String> SIC_CODES_LIST = Arrays.asList(SIC_CODES);

    @Test
    @DisplayName("Test advanced search returns results successfully")
    void testAdvancedSearch() throws Exception{

        Company company = createCompany();

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME);

        when(mockAdvancedSearchRequests.getCompanies(advancedSearchQueryParams, REQUEST_ID)).thenReturn(createSearchHits());
        when(mockElasticSearchResponseMapper.mapAdvancedSearchResponse(createSearchHits().getAt(0))).thenReturn(company);
        when(mockElasticSearchResponseMapper.mapAdvancedTopHit(company)).thenReturn(createTopHit());

        SearchResults<Company> searchResults =
                searchRequestService.getSearchResults(advancedSearchQueryParams, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(COMPANY_NAME, searchResults.getTopHit().getCompanyName());
        assertEquals(SIC_CODES_LIST, searchResults.getTopHit().getSicCodes());
        assertEquals("search#advanced-search", searchResults.getKind());
    }

    @Test
    @DisplayName("Test advanced search returns no results")
    void testAdvancedSearchNoResults() throws Exception{

        Company company = createCompany();

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();

        when(mockAdvancedSearchRequests.getCompanies(advancedSearchQueryParams, REQUEST_ID)).thenReturn(createEmptySearchHits());

        SearchResults<Company> searchResults =
                searchRequestService.getSearchResults(advancedSearchQueryParams, REQUEST_ID);

        assertNotNull(searchResults);
        assertNotNull(searchResults.getItems());
    }

    @Test
    @DisplayName("Test search request throws exception")
    void testThrowException() throws Exception {

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME);

        when(mockAdvancedSearchRequests.getCompanies(advancedSearchQueryParams, REQUEST_ID)).
                thenThrow(IOException.class);

        assertThrows(SearchException.class,
                () -> searchRequestService.getSearchResults(advancedSearchQueryParams, REQUEST_ID));
    }

    private SearchHits createSearchHits() {
        BytesReference source = new BytesArray(
                "{" + "\"company_type\": \"ltd\","
                    + "\"kind\": \"kind\","
                    + "\"current_company\" : {"
                        + "\"sic_codes\" : ["
                            + "\"99960\""
                        + "],"
                        + "\"company_number\" : \"00000000\","
                        + "\"address\" : {"
                            + "\"premises\" : \"premises\""
                            + "\"address_line_1\" : \"address line 1\""
                            + "\"address_line_2\" : \"address line 2\""
                            + "\"locality\" : \"locality\""
                            + "\"postal_code\" : \"postal code\""
                            + "\"region\" : \"region\""
                        + "},"
                        + "\"date_of_creation\" : \"19890501\""
                        + "\"company_status\" : \"active\","
                        + "\"corporate_name\" : \"TEST COMPANY\""

                    + "},"
                    + "\"links\" : {"
                        + "\"self\" : \"/company/00000000\""
                    + "}"
                + "}");
        SearchHit hit = new SearchHit(1);
        hit.sourceRef(source);
        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits(new SearchHit[] { hit }, totalHits, 10);
    }

    private SearchHits createEmptySearchHits() {
        TotalHits totalHits = new TotalHits(0, EQUAL_TO);
        return new SearchHits(new SearchHit[] {}, totalHits, 0);
    }

    private Company createCompany() {
        Company company = new Company();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setCompanyType(COMPANY_TYPE);
        company.setSicCodes(SIC_CODES_LIST);
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
        topHit.setSicCodes(SIC_CODES_LIST);
        topHit.setKind(KIND);

        Links links = new Links();
        links.setCompanyProfile(COMPANY_PROFILE_LINK);
        topHit.setLinks(links);

        return topHit;
    }
}
