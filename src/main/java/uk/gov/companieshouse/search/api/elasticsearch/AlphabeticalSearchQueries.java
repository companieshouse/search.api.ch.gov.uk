package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.springframework.stereotype.Component;

@Component
public class AlphabeticalSearchQueries extends AbstractSearchQuery {

    public QueryBuilder createOrderedAlphaKeySearchQuery(String orderedAlphaKey) {

        QueryBuilder query = QueryBuilders.matchQuery("items.ordered_alpha_key", orderedAlphaKey);
        System.err.println("*** Query is ***");
        System.err.println(query);
        return query;
    }

    public QueryBuilder createOrderedAlphaKeyKeywordQuery(String orderedAlphaKey) {

        QueryBuilder query = QueryBuilders.prefixQuery("items.ordered_alpha_key.keyword", orderedAlphaKey);
        System.err.println("*** Query is ***");
        System.err.println(query);

        return query;
    }

    public QueryBuilder createStartsWithQuery(String corporateName) {

        QueryBuilder query = QueryBuilders.matchPhrasePrefixQuery("items.corporate_name.startswith", corporateName);

        System.err.println("*** Query is ***");
        System.err.println(query);

        return query;
    }

}
