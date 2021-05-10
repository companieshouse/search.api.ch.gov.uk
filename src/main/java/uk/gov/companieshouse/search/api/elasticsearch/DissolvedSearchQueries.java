package uk.gov.companieshouse.search.api.elasticsearch;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

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

    public QueryBuilder createBestMatchQuery(String companyName) {
        return QueryBuilders.matchQuery("short_name.company_name_best_match", companyName);
    }

    public QueryBuilder createPreviousNamesBestMatchQuery(String companyName) {

        BoolQueryBuilder boolQueryBuilder =
                QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("previous_company_names.short_name.company_name_best_match", companyName));

        return QueryBuilders.nestedQuery("previous_company_names", boolQueryBuilder, ScoreMode.Avg)
                .innerHit(new InnerHitBuilder());
    }
}