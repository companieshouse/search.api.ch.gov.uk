package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

@Component
public class AlphabeticalSearchQueries extends SearchQuery {

    public QueryBuilder createOrderedAlphaKeySearchQuery(String orderedAlphaKey) {

        return QueryBuilders.matchQuery("items.ordered_alpha_key", orderedAlphaKey);
    }

    public QueryBuilder createOrderedAlphaKeyKeywordQuery(String orderedAlphaKey) {


        return QueryBuilders.prefixQuery("items.ordered_alpha_key.keyword", orderedAlphaKey);
    }

    public QueryBuilder createStartsWithQuery(String corporateName) {

        return QueryBuilders.matchPhrasePrefixQuery("items.corporate_name.startswith", corporateName);
    }
}
