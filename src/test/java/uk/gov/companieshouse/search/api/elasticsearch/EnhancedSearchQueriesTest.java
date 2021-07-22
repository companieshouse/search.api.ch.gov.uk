package uk.gov.companieshouse.search.api.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

import java.time.LocalDate;

class EnhancedSearchQueriesTest {

    private EnhancedSearchQueries enhancedSearchQueries = new EnhancedSearchQueries();

    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String LOCATION = "TEST LOCATION";
    private static final LocalDate INCORPORATED_FROM = LocalDate.of(2000, 1, 1);

    private static final String COMPANY_NAME_MUST_CONTAIN_FIELD = "current_company.corporate_name";
    private static final String LOCATION_MATCH_FIELD = "current_company.full_address";
    private static final String INCORPORATION_DATE_MATCH_FIELD = "current_company.date_of_creation";

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

    @Test
    @DisplayName("Create location match query")
    void locationMatchQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setLocation(LOCATION);

        QueryBuilder queryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(queryBuilder);
        assertTrue(queryBuilder.toString().contains(LOCATION_MATCH_FIELD));
        assertTrue(queryBuilder.toString().contains(LOCATION));
    }

    @Test
    @DisplayName("Create incorporated from match query")
    void incorporatedFromFromQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setIncorporatedFrom(INCORPORATED_FROM);

        QueryBuilder queryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(queryBuilder);
        assertTrue(queryBuilder.toString().contains(INCORPORATION_DATE_MATCH_FIELD));
        assertTrue(queryBuilder.toString().contains(INCORPORATED_FROM.toString()));
    }

    @Test
    @DisplayName("No query parameters present")
    void noQueryParametersPresent() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();

        BoolQueryBuilder boolQueryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(boolQueryBuilder);
        assertFalse(boolQueryBuilder.toString().contains(COMPANY_NAME_MUST_CONTAIN_FIELD));
        assertFalse(boolQueryBuilder.toString().contains(COMPANY_NAME));
    }

}