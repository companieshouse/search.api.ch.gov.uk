package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.ObjectMapperException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    @Autowired
    private EnvironmentReader environmentReader;
    @Autowired
    private RestClientService searchRestClient;

    private static final String INDEX = "ALPHABETICAL_SEARCH_INDEX";

    private static final String RESULTS_SIZE = "ALPHABETICAL_SEARCH_RESULT_MAX";

    private static final String ALPHABETICAL_SEARCH = "Alphabetical Search: ";

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

   /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults createSearchRequest(String corporateName, String requestId) throws IOException {

        LOG.info(ALPHABETICAL_SEARCH + "Creating search request for: " + corporateName + " for user with Id: " + requestId);

        List<String> results = new ArrayList<>();

        SearchRequest searchRequestBestMatch = createBaseSearchRequest(requestId);
        searchRequestBestMatch.source(bestMatchSourceBuilder(createBestMatchSearchQuery(corporateName), SortOrder.ASC));

        SearchResponse searchResponse = searchRestClient.searchRestClient(searchRequestBestMatch);
        SearchHits hits = searchResponse.getHits();
        String bestMatch = "";

        if (hits.getTotalHits().value == 0) {
            SearchRequest searchRequestStartsWith = createBaseSearchRequest(requestId);

            searchRequestStartsWith.source(bestMatchSourceBuilder(createStartsWithQuery(corporateName), SortOrder.ASC));
            SearchResponse searchResponseStartsWith = searchRestClient.searchRestClient(searchRequestStartsWith);
            hits = searchResponseStartsWith.getHits();

        }

        if (hits.getTotalHits().value > 0) {
            String corporateStripped = getCorporateNameStripped(hits.getHits()[0]);

            System.out.println("########## CORPORATE STRIPPED ##########" + corporateStripped);
            Optional<Company> companyTopHit;

            try {
                // extract the highest matched name from position 0 as we know there is only one.
                companyTopHit = Optional.of(new ObjectMapper()
                    .readValue(hits.getAt(0).getSourceAsString(), Company.class));

                bestMatch = companyTopHit.map(Company::getItems)
                    .map(Items::getCorporateName)
                    .orElse("");

                SearchRequest searchAlphabetic = createBaseSearchRequest(requestId);

                searchAlphabetic.source(alphabeticalSourceBuilder(corporateStripped, createAlphabeticalQuery(), SortOrder.DESC));
                SearchResponse searchResponseAfterDesc = searchRestClient.searchRestClient(searchAlphabetic);
                hits = searchResponseAfterDesc.getHits();

                hits.forEach(h -> {System.out.println(getCorporateName(h)); results.add(getCorporateName(h));});
                results.add(bestMatch);
                System.out.println("########## BEST MATCH ##########" + bestMatch);

                searchAlphabetic.source(alphabeticalSourceBuilder(corporateStripped, createAlphabeticalQuery(), SortOrder.ASC));
                SearchResponse searchResponseAfterAsc = searchRestClient.searchRestClient(searchAlphabetic);
                hits = searchResponseAfterAsc.getHits();
                hits.forEach(h -> {System.out.println(getCorporateName(h)); results.add(getCorporateName(h));});
            } catch (IOException e) {
                LOG.error(ALPHABETICAL_SEARCH + "failed to map highest map to company object for: " + corporateName, e);
                throw new ObjectMapperException("error occurred reading data for highest match from " +
                    "searchHits", e);
            }
        }

        return new SearchResults("", bestMatch, results);
    }

    private String getCorporateNameStripped(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return (String) sourceAsMap.get("corporate_stripped");
    }

    private String getCorporateName(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> items = (Map<String, Object>) sourceAsMap.get("items");
        return (String) (items.get("corporate_name"));
    }

    private SearchRequest createBaseSearchRequest(String requestId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(INDEX));
        searchRequest.preference(requestId);

        return searchRequest;
    }

    private SearchSourceBuilder bestMatchSourceBuilder(QueryBuilder queryBuilder, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.sort("corporate_stripped", sortOrder);

        return sourceBuilder;
    }

    private SearchSourceBuilder alphabeticalSourceBuilder(String corporateStripped, QueryBuilder queryBuilder, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.searchAfter(new Object[]{corporateStripped});
        sourceBuilder.sort("corporate_stripped", sortOrder);

        return sourceBuilder;
    }

    private QueryBuilder createBestMatchSearchQuery(String corporateName) {

        LOG.info(ALPHABETICAL_SEARCH + "Running best match query for: " + corporateName);

        return QueryBuilders.matchQuery("items.corporate_name.first_token", corporateName);
    }

    private QueryBuilder createStartsWithQuery(String corporateName) {

        LOG.info(ALPHABETICAL_SEARCH + "Running starts with query for: " + corporateName);

        return QueryBuilders.matchPhrasePrefixQuery("items.corporate_name.startswith", corporateName);
    }

    private QueryBuilder createAlphabeticalQuery() {

        return QueryBuilders.matchAllQuery();
    }
}
