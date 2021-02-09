package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class SearchQuery {
	
    public QueryBuilder createMatchAllQuery() {

        return QueryBuilders.matchAllQuery();
    }
}
