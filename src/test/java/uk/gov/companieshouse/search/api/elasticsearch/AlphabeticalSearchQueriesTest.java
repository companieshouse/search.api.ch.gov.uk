package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AlphabeticalSearchQueriesTest {

    AlphabeticalSearchQueries alphabeticalSearchQueries = new AlphabeticalSearchQueries();

    @Test
    @DisplayName("Create Ordered AlphaKey Search Query")
    void createOrderedAlphaKeySearchQuery() {
        QueryBuilder queryBuilder =
            alphabeticalSearchQueries.createOrderedAlphaKeySearchQuery("orderAlphakey");

        assertNotNull(queryBuilder);
    }

    @Test
    @DisplayName("Create Ordered AlphaKey Keyword Query")
    void createOrderedAlphaKeyKeywordQuery() {
        QueryBuilder queryBuilder =
            alphabeticalSearchQueries.createOrderedAlphaKeyKeywordQuery("orderAlphakey");

        assertNotNull(queryBuilder);
    }

    @Test
    @DisplayName("Create Starts With Query")
    void createStartsWithQuery() {
        QueryBuilder queryBuilder =
            alphabeticalSearchQueries.createStartsWithQuery("corporateNameStartsWith");

        assertNotNull(queryBuilder);
    }

    @Test
    @DisplayName("Create Alphabetical Query")
    void createAlphabeticalQuery() {
        QueryBuilder queryBuilder =
            alphabeticalSearchQueries.createMatchAllQuery();

        assertNotNull(queryBuilder);
    }
}
