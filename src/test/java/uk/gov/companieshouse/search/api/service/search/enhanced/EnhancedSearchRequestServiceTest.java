package uk.gov.companieshouse.search.api.service.search.enhanced;

import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import uk.gov.companieshouse.search.api.elasticsearch.EnhancedSearchRequests;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.service.search.impl.enhanced.EnhancedSearchRequestService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnhancedSearchRequestServiceTest {

    @Mock
    private EnhancedSearchRequests mockEnhancedSearchRequests;

    @Mock
    private ElasticSearchResponseMapper mockElasticSearchResponseMapper;

    @InjectMocks
    private EnhancedSearchRequestService searchRequestService;

    private static final String COMPANY_NAME = "test company";
    private static final String REQUEST_ID = "123456789";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String COMPANY_STATUS = "dissolved";
    private static final String COMPANY_TYPE = "ltd";
    private static final String COMPANY_PROFILE_LINK = "/company/00000000";
    private static final String KIND = "searchresults#enhanced-search-company";

    @Test
    @DisplayName("Test enhanced search returns results successfully")
    void testEnhancedSearch() throws Exception{

        Company company = createCompany();

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(COMPANY_NAME);

        when(mockEnhancedSearchRequests.getCompanies(enhancedSearchQueryParams, REQUEST_ID)).thenReturn(createSearchHits());
        when(mockElasticSearchResponseMapper.mapEnhancedSearchResponse(createSearchHits().getAt(0))).thenReturn(company);
        when(mockElasticSearchResponseMapper.mapEnhancedTopHit(company)).thenReturn(createTopHit());

        SearchResults<Company> searchResults =
                searchRequestService.getSearchResults(enhancedSearchQueryParams, REQUEST_ID);

        assertNotNull(searchResults);
        assertEquals(COMPANY_NAME, searchResults.getTopHit().getCompanyName());
        assertEquals("search#enhanced-search", searchResults.getKind());
    }

    @Test
    @DisplayName("Test search request throws exception")
    void testThrowException() throws Exception {

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(COMPANY_NAME);

        when(mockEnhancedSearchRequests.getCompanies(enhancedSearchQueryParams, REQUEST_ID)).
                thenThrow(IOException.class);

        assertThrows(SearchException.class,
                () -> searchRequestService.getSearchResults(enhancedSearchQueryParams, REQUEST_ID));
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
