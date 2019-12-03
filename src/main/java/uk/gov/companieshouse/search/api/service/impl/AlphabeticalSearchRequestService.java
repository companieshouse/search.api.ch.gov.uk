package uk.gov.companieshouse.search.api.service.impl;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.service.SearchRequestService;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    private static final String ALPHA_SEARCH = "alpha_search";

    private static final int RESULTS_SIZE = 1000;
    private static final int AGGS_HIGHEST_MATCH_SIZE = 1;
    private static final String HIGHEST_MATCH = "highest_match";


    /**
     * {@inheritDoc}
     */
    @Override
    public SearchRequest createSearchRequest(String corporateName) {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ALPHA_SEARCH);
        searchRequest.source(createSource(corporateName));

        return searchRequest;
    }

    private SearchSourceBuilder createSource(String corporateName) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(RESULTS_SIZE);
        sourceBuilder.query(createAlphabeticalSearchQuery(corporateName));
        sourceBuilder.aggregation(createAggregation(HIGHEST_MATCH, AGGS_HIGHEST_MATCH_SIZE));

        return sourceBuilder;
    }

    private AggregationBuilder createAggregation(String aggregationName, int size) {
        return AggregationBuilders
            .topHits(aggregationName)
            .size(size);
    }

    private QueryBuilder createAlphabeticalSearchQuery(String corporateName) {

        return QueryBuilders.boolQuery()
            .should(QueryBuilders
                .matchQuery("items.corporate_name_start.edge_ngram", corporateName).fuzziness(2))
            .should(QueryBuilders
                .matchQuery("items.corporate_name_start", corporateName).boost(5))
            .should(QueryBuilders
                .matchPhraseQuery("items.corporate_name_start", corporateName));
    }
}
