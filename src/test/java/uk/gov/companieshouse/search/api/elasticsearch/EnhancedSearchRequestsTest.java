package uk.gov.companieshouse.search.api.elasticsearch;

import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchResponseSections;
import org.elasticsearch.action.search.ShardSearchFailure;
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
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;
import uk.gov.companieshouse.search.api.service.rest.impl.EnhancedSearchRestClientService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnhancedSearchRequestsTest {

    @InjectMocks
    private EnhancedSearchRequests enhancedSearchRequests;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @Mock
    private EnhancedSearchQueries mockEnhancedSearchQueries;

    @Mock
    private EnhancedSearchRestClientService mockSearchRestClient;

    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String ENV_READER_RESULT = "ENHANCED_SEARCH_INDEX";
    private static final String REQUEST_ID = "123456789";

    @Test
    @DisplayName("Get company number (must contain) response")
    void getCompanyNumberMustContainSuccessful() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT);
        when(mockSearchRestClient.search(any(SearchRequest.class))).thenReturn(createSearchResponse());

        SearchHits searchHits = enhancedSearchRequests.getCompanies(createEnhancedSearchQueryParams(), REQUEST_ID);

        assertNotNull(searchHits);
        assertEquals(1, searchHits.getTotalHits().value);
    }

    private SearchResponse createSearchResponse() {
        BytesReference source = new BytesArray("{test}");
        SearchHit hit = new SearchHit(1);
        hit.sourceRef(source);
        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        SearchHits hits = new SearchHits(new SearchHit[]{hit}, totalHits, 10);
        SearchResponseSections searchResponseSections = new SearchResponseSections(hits, null, null, false, null, null, 5);
        SearchResponse.Clusters clusters = new SearchResponse.Clusters(1, 1, 0);
        SearchResponse searchResponse = new SearchResponse(searchResponseSections, "", 1, 1, 0, 8, new ShardSearchFailure[]{}, clusters);
        return searchResponse;
    }

    private EnhancedSearchQueryParams createEnhancedSearchQueryParams() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();

        enhancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME);

        return enhancedSearchQueryParams;

    }
}
