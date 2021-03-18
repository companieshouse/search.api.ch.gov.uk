package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.util.StringTokeniserUtil;

import java.util.List;

@Component
public class DissolvedSearchQueries extends AbstractSearchQuery {

    public QueryBuilder createOrderedAlphaKeySearchQuery(String orderedAlphaKey) {
        return QueryBuilders.matchQuery("ordered_alpha_key", orderedAlphaKey);
    }

    public QueryBuilder createOrderedAlphaKeyKeywordQuery(String orderedAlphaKey) {
        return QueryBuilders.prefixQuery("ordered_alpha_key.keyword", orderedAlphaKey);
    }

    public QueryBuilder createStartsWithQuery(String corporateName) {
        return QueryBuilders.matchPhrasePrefixQuery("company_name.startswith", corporateName);
    }
}
