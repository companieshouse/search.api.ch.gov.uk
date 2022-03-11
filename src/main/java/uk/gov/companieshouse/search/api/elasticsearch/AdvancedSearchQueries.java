package uk.gov.companieshouse.search.api.elasticsearch;

import java.time.LocalDate;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;

@Component
public class AdvancedSearchQueries {

    public BoolQueryBuilder buildAdvancedSearchQuery(AdvancedSearchQueryParams queryParams) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder;

        if (queryParams.getCompanyNameIncludes() != null) {
            queryBuilder = QueryBuilders.matchQuery("current_company.corporate_name",
                    queryParams.getCompanyNameIncludes()).operator(Operator.AND);

            boolQueryBuilder.filter(queryBuilder);
        }

        if (queryParams.getLocation() != null) {
            queryBuilder = QueryBuilders.matchQuery("current_company.full_address",
                    queryParams.getLocation()).operator(Operator.AND);

            boolQueryBuilder.filter(queryBuilder);
        }

        if (queryParams.getCompanyStatusList() != null) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("current_company.company_status.keyword",
                queryParams.getCompanyStatusList()));
        }

        addRangeQueryDates(boolQueryBuilder,
            queryParams.getIncorporatedFrom(),
            queryParams.getIncorporatedTo(),
            "current_company.date_of_creation");

        addRangeQueryDates(boolQueryBuilder,
            queryParams.getDissolvedFrom(),
            queryParams.getDissolvedTo(),
            "current_company.date_of_cessation");

        if (queryParams.getSicCodes() != null) {

            queryBuilder = QueryBuilders.termsQuery("current_company.sic_codes",
                queryParams.getSicCodes());

            boolQueryBuilder.filter(queryBuilder);
        }

        if (queryParams.getCompanyTypeList() != null) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("company_type", queryParams.getCompanyTypeList()));
        }

        if (queryParams.getCompanySubtypeList() != null) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("company_subtype", queryParams.getCompanySubtypeList()));
        }

        if (queryParams.getCompanyNameExcludes() != null) {
            queryBuilder = QueryBuilders.matchQuery("current_company.corporate_name",
                queryParams.getCompanyNameExcludes())
                .operator(Operator.OR);

            boolQueryBuilder.mustNot(queryBuilder);
        }

        return boolQueryBuilder;
    }

    private void addRangeQueryDates(BoolQueryBuilder boolQueryBuilder, LocalDate from, LocalDate to, String fieldName) {
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
