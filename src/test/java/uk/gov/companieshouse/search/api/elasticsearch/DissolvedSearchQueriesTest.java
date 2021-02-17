package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DissolvedSearchQueriesTest {

    DissolvedSearchQueries dissolvedSearchQueries = new DissolvedSearchQueries();

    @Test
    @DisplayName("Create Ordered AlphaKey Search Query")
    void createOrderedAlphaKeySearchQuery() {
        QueryBuilder queryBuilder =
                dissolvedSearchQueries.createOrderedAlphaKeySearchQuery("orderAlphakey");

        assertNotNull(queryBuilder);
    }

    @Test
    @DisplayName("Create Ordered AlphaKey Keyword Query")
    void createOrderedAlphaKeyKeywordQuery() {
        QueryBuilder queryBuilder =
                dissolvedSearchQueries.createOrderedAlphaKeyKeywordQuery("orderAlphakey");

        assertNotNull(queryBuilder);
    }

    @Test
    @DisplayName("Create Starts With Query")
    void createStartsWithQuery() {
        QueryBuilder queryBuilder =
                dissolvedSearchQueries.createStartsWithQuery("corporateNameStartsWith");

        assertNotNull(queryBuilder);
    }

    @Test
    @DisplayName("Create Alphabetical Query")
    void createAlphabeticalQuery() {
        QueryBuilder queryBuilder =
                dissolvedSearchQueries.createMatchAllQuery();

        assertNotNull(queryBuilder);
    }
}
