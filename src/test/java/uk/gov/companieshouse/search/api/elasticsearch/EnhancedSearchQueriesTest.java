package uk.gov.companieshouse.search.api.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

class EnhancedSearchQueriesTest {

    private EnhancedSearchQueries enhancedSearchQueries = new EnhancedSearchQueries();

    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String LOCATION = "TEST LOCATION";
    private static final LocalDate INCORPORATED_FROM = LocalDate.of(2000, 1, 1);
    private static final LocalDate INCORPORATED_TO = LocalDate.of(2002, 2, 2);
    private static final String SIC_CODES = "99960";
    private static final String COMPANY_NAME_MUST_CONTAIN_FIELD = "current_company.corporate_name";
    private static final String LOCATION_MATCH_FIELD = "current_company.full_address";
    private static final String INCORPORATION_DATE_MATCH_FIELD = "current_company.date_of_creation";
    private static final String SIC_CODES_MATCH_FIELD = "current_company.sic_codes";

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
    void incorporatedFromQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setIncorporatedFrom(INCORPORATED_FROM);

        QueryBuilder queryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(queryBuilder);
        assertTrue(queryBuilder.toString().contains(INCORPORATION_DATE_MATCH_FIELD));
        assertTrue(queryBuilder.toString().contains(INCORPORATED_FROM.toString()));
    }

    @Test
    @DisplayName("Create incorporated to match query")
    void incorporatedToQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setIncorporatedTo(INCORPORATED_TO);

        QueryBuilder queryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(queryBuilder);
        assertTrue(queryBuilder.toString().contains(INCORPORATION_DATE_MATCH_FIELD));
        assertTrue(queryBuilder.toString().contains(INCORPORATED_TO.toString()));
    }

    @Test
    @DisplayName("Create sic codes match query")
    void sicCodesMatchQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setSicCodes(SIC_CODES);

        QueryBuilder queryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(queryBuilder);
        assertTrue(queryBuilder.toString().contains(SIC_CODES_MATCH_FIELD));
        assertTrue(queryBuilder.toString().contains(SIC_CODES));
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
