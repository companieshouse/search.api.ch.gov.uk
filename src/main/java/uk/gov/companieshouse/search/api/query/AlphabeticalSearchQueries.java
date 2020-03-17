package uk.gov.companieshouse.search.api.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

@Component
public class AlphabeticalSearchQueries {

    public QueryBuilder createOrderedAlphaKeySearchQuery(String orderedAlphaKey) {

        return QueryBuilders.matchQuery("items.ordered_alpha_key", orderedAlphaKey);
    }

    public QueryBuilder createOrderedAlphaKeyKeywordQuery(String orderedAlphaKey) {


        return QueryBuilders.prefixQuery("items.ordered_alpha_key.keyword", orderedAlphaKey);
    }

    public QueryBuilder createStartsWithQuery(String corporateName) {

        return QueryBuilders.matchPhrasePrefixQuery("items.corporate_name.startswith", corporateName);
    }

    public QueryBuilder createAlphabeticalQuery() {

        return QueryBuilders.matchAllQuery();
    }
}
