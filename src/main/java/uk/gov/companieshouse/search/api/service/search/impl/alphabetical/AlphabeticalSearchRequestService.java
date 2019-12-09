package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    private static final String ALPHA_SEARCH = "alpha_search";

    private static final int RESULTS_SIZE = 1000;
    private static final int AGGS_HIGHEST_MATCH_SIZE = 1;
    private static final String HIGHEST_MATCH = "highest_match";

    private static final String ALPHABETICAL_SEARCH = "Alphabetical Search: ";

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchRequest createSearchRequest(String corporateName) {


        LOG.info(ALPHABETICAL_SEARCH + "Creating search request for: " + corporateName);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ALPHA_SEARCH);
        searchRequest.source(createSource(corporateName));

        return searchRequest;
    }

    private SearchSourceBuilder createSource(String corporateName) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(RESULTS_SIZE);
        sourceBuilder.query(createAlphabeticalSearchQuery(corporateName));
        sourceBuilder.aggregation(createAggregation(HIGHEST_MATCH, AGGS_HIGHEST_MATCH_SIZE, corporateName));

        return sourceBuilder;
    }

    private AggregationBuilder createAggregation(String aggregationName, int size, String corporateName) {

        LOG.info(ALPHABETICAL_SEARCH + "Adding top hit aggregation for: " + corporateName);

        return AggregationBuilders
            .topHits(aggregationName)
            .size(size);
    }

    private QueryBuilder createAlphabeticalSearchQuery(String corporateName) {

        LOG.info(ALPHABETICAL_SEARCH + "Adding query for: " + corporateName);

        return QueryBuilders.boolQuery()
            .should(QueryBuilders
                .matchQuery("items.corporate_name_start.edge_ngram", corporateName).fuzziness(2))
            .should(QueryBuilders
                .matchQuery("items.corporate_name_start", corporateName).boost(5))
            .should(QueryBuilders
                .matchPhraseQuery("items.corporate_name_start", corporateName));
    }
}