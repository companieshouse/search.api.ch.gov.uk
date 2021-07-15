package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

@Component
public class EnhancedSearchQueries {

    public BoolQueryBuilder buildEnhancedSearchQuery(EnhancedSearchQueryParams queryParams) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (queryParams.getCompanyName() != null) {
            boolQueryBuilder.filter(
                    QueryBuilders.matchQuery("current_company.corporate_name", queryParams.getCompanyName()));
        }

        return boolQueryBuilder;
    }
}
