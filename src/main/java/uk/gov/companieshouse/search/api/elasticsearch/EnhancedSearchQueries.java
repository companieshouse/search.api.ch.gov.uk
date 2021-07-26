package uk.gov.companieshouse.search.api.elasticsearch;

import java.time.LocalDate;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

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
                queryParams.getIncorporatedTo(),
                "current_company.date_of_creation");

        if (queryParams.getSicCodes() != null) {

            queryBuilder = QueryBuilders.termQuery("current_company.sic_codes",
                    queryParams.getSicCodes());

            boolQueryBuilder.filter(queryBuilder);
        }

        return boolQueryBuilder;
    }

    private void addRangeQueryDates(BoolQueryBuilder boolQueryBuilder, LocalDate from, LocalDate to,  String fieldName) {
        if (from == null && to == null) {
            return;
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(fieldName);

        if (from != null) {
            rangeQueryBuilder.gte(from.toString());
        }
        if (to != null) {
            rangeQueryBuilder.lte(to.toString());
        }

        boolQueryBuilder.filter(rangeQueryBuilder);
    }
}
