package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String INDEX = "ALPHABETICAL_SEARCH_INDEX";

    private static final String RESULTS_SIZE = "ALPHABETICAL_SEARCH_RESULT_MAX";
    private static final int AGGS_HIGHEST_MATCH_SIZE = 1;
    private static final String HIGHEST_MATCH = "highest_match";

    private static final String ALPHABETICAL_SEARCH = "Alphabetical Search: ";

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchRequest createSearchRequest(String corporateName, String requestId) {

        LOG.info(ALPHABETICAL_SEARCH + "Creating search request for: " + corporateName + " for user with Id: " + requestId);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(INDEX));
        searchRequest.preference(requestId);
        searchRequest.source(createSource(corporateName));

        return searchRequest;
    }

    private SearchSourceBuilder createSource(String corporateName) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
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

        CharSequence spaceChar = " ";
        String corporateNameFirstWord = corporateName.contains(spaceChar) ?
                corporateName.substring(0, corporateName.indexOf(spaceChar.toString())) : corporateName;

        return QueryBuilders.boolQuery()
                .should(QueryBuilders.prefixQuery("items.corporate_name_start", corporateName).boost(5))
                .should(QueryBuilders.queryStringQuery(corporateNameFirstWord).enablePositionIncrements(true)
                        .allowLeadingWildcard(false).autoGenerateSynonymsPhraseQuery(false))
                .should(QueryBuilders.matchQuery("items.corporate_name_start.edge_ngram", corporateNameFirstWord).fuzziness(2));
    }
}
