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
import java.util.Arrays;
import java.util.List;

class EnhancedSearchQueriesTest {

    private EnhancedSearchQueries enhancedSearchQueries = new EnhancedSearchQueries();

    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String LOCATION = "TEST LOCATION";
    private static final String ACTIVE_COMPANY_STATUS = "active";
    private static final String SIC_CODES = "99960";
    private static final String LTD_COMPANY_TYPE = "ltd";
    private static final String PLC_COMPANY_TYPE = "plc";
    private static final String COMPANY_NAME_EXCLUDES = "test name excludes";
    private static final LocalDate INCORPORATED_FROM = LocalDate.of(2000, 1, 1);
    private static final LocalDate INCORPORATED_TO = LocalDate.of(2002, 2, 2);
    private static final List<String> COMPANY_STATUS_LIST = Arrays.asList(ACTIVE_COMPANY_STATUS);
    private static final List<String> SIC_CODES_LIST = Arrays.asList(SIC_CODES);
    private static final List<String> COMPANY_TYPES_LIST = Arrays.asList(LTD_COMPANY_TYPE, PLC_COMPANY_TYPE);

    // Elastic search fields
    private static final String COMPANY_NAME_MUST_CONTAIN_FIELD = "current_company.corporate_name";
    private static final String LOCATION_MATCH_FIELD = "current_company.full_address";
    private static final String INCORPORATION_DATE_MATCH_FIELD = "current_company.date_of_creation";
    private static final String COMPANY_STATUS_MATCH_FIELD = "current_company.company_status.keyword";
    private static final String SIC_CODES_MATCH_FIELD = "current_company.sic_codes";
    private static final String COMPANY_TYPE_MATCH_FIELD = "company_type";

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
    @DisplayName("Create company status match query")
    void companyStatusQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyStatusList(COMPANY_STATUS_LIST);

        QueryBuilder queryBuilder =
            enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(queryBuilder);
        assertTrue(queryBuilder.toString().contains(COMPANY_STATUS_MATCH_FIELD));
        assertTrue(queryBuilder.toString().contains(ACTIVE_COMPANY_STATUS));
    }

    @Test
    @DisplayName("Create sic codes match query")
    void sicCodesMatchQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setSicCodes(SIC_CODES_LIST);

        QueryBuilder queryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(queryBuilder);
        assertTrue(queryBuilder.toString().contains(SIC_CODES_MATCH_FIELD));
        assertTrue(queryBuilder.toString().contains(SIC_CODES));
    }

    @Test
    @DisplayName("Create company type match query")
    void companyTypeQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyTypeList(COMPANY_TYPES_LIST);

        QueryBuilder queryBuilder =
            enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(queryBuilder);
        assertTrue(queryBuilder.toString().contains(COMPANY_TYPE_MATCH_FIELD));
        assertTrue(queryBuilder.toString().contains(LTD_COMPANY_TYPE));
        assertTrue(queryBuilder.toString().contains(PLC_COMPANY_TYPE));
    }

    @Test
    @DisplayName("Create company name excludes query")
    void companyNameExcludesQuery() {
        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyNameExcludes(COMPANY_NAME_EXCLUDES);

        BoolQueryBuilder boolQueryBuilder =
                enhancedSearchQueries.buildEnhancedSearchQuery(enhancedSearchQueryParams);

        assertNotNull(boolQueryBuilder);
        assertTrue(boolQueryBuilder.toString().contains(COMPANY_NAME_EXCLUDES));
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
