package uk.gov.companieshouse.search.api.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AlphabeticalSearchQueries extends AbstractSearchQuery {

    public QueryBuilder createOrderedAlphaKeySearchQuery(String orderedAlphaKey) {

        return QueryBuilders.matchQuery("items.ordered_alpha_key", orderedAlphaKey);
    }

    public QueryBuilder createOrderedAlphaKeyKeywordQuery(String orderedAlphaKey) {


        return QueryBuilders.prefixQuery("items.ordered_alpha_key.keyword", orderedAlphaKey);
    }

    public QueryBuilder createStartsWithQuery(String corporateName) {

        return QueryBuilders.matchPhrasePrefixQuery("items.corporate_name.startswith", corporateName);
    }

    public QueryBuilder createNoResultsFoundQuery(String orderedAlphaKey) {

        List<String> tokens = new ArrayList<>();

        for (int i=0; i < orderedAlphaKey.length(); i++) {

            if (i != orderedAlphaKey.length() - 1) {
                String resultString = orderedAlphaKey.substring(0, orderedAlphaKey.length() - i);
                tokens.add(resultString);
            }
        }

        Collections.reverse(tokens);

        return QueryBuilders.termsQuery("items.ordered_alpha_key.keyword", tokens);
    }
}
