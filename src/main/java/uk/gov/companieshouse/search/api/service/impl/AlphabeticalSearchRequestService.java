package uk.gov.companieshouse.search.api.service.impl;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.service.SearchRequestService;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    private static final String ALPHA_SEARCH = "alpha_search";

    private static final int RESULTS_SIZE = 0;
    private static final int AGGS_TOP_HITS_ALPHABETICAL_SIZE = 100;
    private static final int AGGS_HIGHEST_MATCH_SIZE = 1;

    private static final String HIGHEST_MATCH = "highest_match";
    private static final String TOP_HITS_ALPHABETICAL = "top_hits_alphabetical";

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
        sourceBuilder.aggregation(createHighestMatchAggregation());
        sourceBuilder.aggregation(createTopHitsAlphabetically());

        return sourceBuilder;
    }

    private AggregationBuilder createHighestMatchAggregation() {
        return AggregationBuilders
            .topHits(HIGHEST_MATCH)
            .size(AGGS_HIGHEST_MATCH_SIZE);
    }

    private AggregationBuilder createTopHitsAlphabetically() {

        return AggregationBuilders
            .topHits(TOP_HITS_ALPHABETICAL)
            .size(AGGS_TOP_HITS_ALPHABETICAL_SIZE)
            .sort(new FieldSortBuilder("items.corporate_name_start.sort")
                .order(SortOrder.ASC));
    }

    private QueryBuilder createAlphabeticalSearchQuery(String corporateName) {

        return QueryBuilders.boolQuery()
            .should(QueryBuilders
                .matchQuery("items.corporate_name_start", corporateName))
            .should(QueryBuilders
                .matchQuery("items.corporate_name_start.edge_ngram", corporateName));
    }
}
