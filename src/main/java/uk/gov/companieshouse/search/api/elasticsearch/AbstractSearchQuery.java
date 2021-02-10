package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public abstract class AbstractSearchQuery {
	
	abstract QueryBuilder createOrderedAlphaKeySearchQuery(String orderedAlphaKey);
	
	abstract QueryBuilder createOrderedAlphaKeyKeywordQuery(String orderedAlphaKey);
	
	abstract QueryBuilder createStartsWithQuery(String corporateName);
	
    public QueryBuilder createMatchAllQuery() {

        return QueryBuilders.matchAllQuery();
    }
}
