package uk.gov.companieshouse.search.api.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

public class EnhancedSearchQueriesTest {

    private EnhancedSearchQueries enhancedSearchQueries = new EnhancedSearchQueries();

    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String COMPANY_NAME_MUST_CONTAIN_FIELD = "current_company.corporate_name";

    @Test
    @DisplayName("Create company name must contain query")
    void companyNameMustContainQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(COMPANY_NAME);

        BoolQueryBuilder boolQueryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(boolQueryBuilder);
        assertTrue(boolQueryBuilder.toString().contains(COMPANY_NAME_MUST_CONTAIN_FIELD));
        assertTrue(boolQueryBuilder.toString().contains(COMPANY_NAME));
    }

}
