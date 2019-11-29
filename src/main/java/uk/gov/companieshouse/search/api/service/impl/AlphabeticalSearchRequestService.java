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

    private static final int NUMBER_OF_RESULTS_TO_RETURN = 10;

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchRequest createSearchRequest(String corporateName, int searchIndexFrom) {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ALPHA_SEARCH);
        searchRequest.source(createSource(corporateName, searchIndexFrom));

        return searchRequest;
    }

    private SearchSourceBuilder createSource(String corporateName, int searchIndexFrom) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(searchIndexFrom);
        sourceBuilder.size(NUMBER_OF_RESULTS_TO_RETURN);
        sourceBuilder.sort(new FieldSortBuilder("items.corporate_name_start.sort")
            .order(SortOrder.ASC));
        sourceBuilder.query(createAlphabeticalSearchQuery(corporateName));
        sourceBuilder.aggregation(createAggregation());

        return sourceBuilder;
    }

    private AggregationBuilder createAggregation() {

        return AggregationBuilders.topHits("closest_match").size(1);
    }

    private QueryBuilder createAlphabeticalSearchQuery(String corporateName) {

        return QueryBuilders.boolQuery()
            .should(QueryBuilders
                .matchQuery("items.corporate_name_start", corporateName))
            .should(QueryBuilders
                .matchQuery("items.corporate_name_start.edge_ngram", corporateName));
    }
}
