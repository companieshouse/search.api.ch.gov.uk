package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

import java.time.LocalDate;

@Component
public class EnhancedSearchQueries {

    public BoolQueryBuilder buildEnhancedSearchQuery(EnhancedSearchQueryParams queryParams) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder;

        if (queryParams.getCompanyName() != null) {
            boolQueryBuilder.filter(
                    QueryBuilders.matchQuery("current_company.corporate_name", queryParams.getCompanyName()));
        }

        if (queryParams.getLocation() != null) {
            queryBuilder = QueryBuilders.matchQuery("current_company.full_address",
                    queryParams.getLocation()).operator(Operator.OR);

            boolQueryBuilder.filter(queryBuilder);
        }

        addRangeQueryDates(boolQueryBuilder,
                queryParams.getIncorporatedFrom(),
                "current_company.date_of_creation");

        return boolQueryBuilder;
    }

    private void addRangeQueryDates(BoolQueryBuilder boolQueryBuilder, LocalDate from, String fieldName) {
        if (from == null) {
            return;
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(fieldName);

        rangeQueryBuilder.gte(from.toString());

        boolQueryBuilder.filter(rangeQueryBuilder);
    }
}
