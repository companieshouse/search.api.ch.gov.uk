package uk.gov.companieshouse.search.api.elasticsearch;

import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvancedSearchRequestsTest {

    @InjectMocks
    private AdvancedSearchRequests advancedSearchRequests;

    @Mock
    private AdvancedSearchQueries mockAdvancedSearchQueries;

    @Mock
    private AdvancedSearchRestClientService mockSearchRestClient;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String ENV_READER_RESULT = "ADVANCED_SEARCH_INDEX";
    private static final String REQUEST_ID = "123456789";

    @Test
    @DisplayName("Get company number (must contain) response")
    void getCompanyNumberMustContainSuccessful() throws Exception {
        when(mockSearchRestClient.search(any(SearchRequest.class))).thenReturn(createSearchResponse());
        when(indices.advanced()).thenReturn(ENV_READER_RESULT);

        SearchHits searchHits = advancedSearchRequests.getCompanies(createAdvancedSearchQueryParams(), REQUEST_ID);

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

    private AdvancedSearchQueryParams createAdvancedSearchQueryParams() {
        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();

        advancedSearchQueryParams.setStartIndex(0);
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME);
        advancedSearchQueryParams.setSize(20);

        return advancedSearchQueryParams;

    }
}
