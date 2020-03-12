package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

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
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        List<Company> results = new ArrayList<>();

        boolean multiwordQuery = false;

        if (corporateName.split("\\s").length > 1) {
            multiwordQuery = true;
        }

        SearchSourceBuilder searchSourceBuilder = multiwordQuery
                ? bestMatchSourceBuilder(createBestMulitwordMatchSearchQuery(corporateName), "items.alpha_key.keyword", SortOrder.ASC)
                    : bestMatchSourceBuilder(createBestMatchSearchQuery(corporateName), "items.alpha_key.keyword", SortOrder.ASC);

        SearchRequest searchRequestBestMatch = createBaseSearchRequest(requestId);
        searchRequestBestMatch.source(searchSourceBuilder);

        SearchResponse searchResponse = searchRestClient.searchRestClient(searchRequestBestMatch);
        SearchHits hits = searchResponse.getHits();
        String topHitCompanyName = "";

        if (hits.getTotalHits().value == 0) {
            LOG.info("A hit was not found for: " + corporateName + ", attempting to find the next best result");
            SearchRequest searchRequestStartsWith = createBaseSearchRequest(requestId);

            searchRequestStartsWith.source(bestMatchSourceBuilder(createStartsWithQuery(corporateName)
                , "items.alpha_key.keyword", SortOrder.ASC));

            SearchResponse searchResponseStartsWith = searchRestClient.searchRestClient(searchRequestStartsWith);
            hits = searchResponseStartsWith.getHits();

        }

        if (hits.getTotalHits().value > 0) {
            LOG.info("One hit or more has been found for: " + corporateName);

            SearchHit topHit = hits.getHits()[0];
            String corporateWithId = getCorporateWithId(hits.getHits()[0]);

            try {
                SearchRequest searchAlphabetic = createBaseSearchRequest(requestId);
                Company topHitCompany = getCompany(topHit);
                topHitCompanyName = topHitCompany.getItems().getCorporateName();

                LOG.info("Retrieving the alphabetically descending results for search query: " + topHitCompanyName);
                searchAlphabetic.source(alphabeticalSourceBuilder(corporateWithId, createAlphabeticalQuery(), SortOrder.DESC));
                SearchResponse searchResponseAfterDesc = searchRestClient.searchRestClient(searchAlphabetic);
                hits = searchResponseAfterDesc.getHits();
                hits.forEach(h -> results.add(getCompany(h)));

                LOG.info("Retrieving the top hit: " + topHitCompanyName);
                results.add(topHitCompany);

                LOG.info("Retrieving the alphabetically ascending results from: " + topHitCompanyName);
                searchAlphabetic.source(alphabeticalSourceBuilder(corporateWithId, createAlphabeticalQuery(), SortOrder.ASC));
                SearchResponse searchResponseAfterAsc = searchRestClient.searchRestClient(searchAlphabetic);
                hits = searchResponseAfterAsc.getHits();
                hits.forEach(h -> results.add(getCompany(h)));

            } catch (IOException e) {
                LOG.error(ALPHABETICAL_SEARCH + "failed to map highest map to company object for: " + corporateName, e);
                throw new ObjectMapperException("error occurred reading data for highest match from " +
                    "searchHits", e);
            }
        }

        return new SearchResults("", topHitCompanyName, results);
    }

    private String getCorporateWithId(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return (String) sourceAsMap.get("corporate_with_type");
    }

    private SearchSourceBuilder bestMatchSourceBuilder(QueryBuilder queryBuilder, String sortField, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.sort(sortField, sortOrder);

        return sourceBuilder;
    }

    private SearchSourceBuilder alphabeticalSourceBuilder(String corporateStripped, QueryBuilder queryBuilder, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.searchAfter(new Object[]{corporateStripped});
        sourceBuilder.sort("corporate_with_type", sortOrder);

        return sourceBuilder;
    }

    private QueryBuilder createBestMatchSearchQuery(String corporateName) {

        LOG.info(ALPHABETICAL_SEARCH + "Running best match query for: " + corporateName);

        return QueryBuilders.matchQuery("items.corporate_name.first_token", corporateName);
    }

    private QueryBuilder createBestMulitwordMatchSearchQuery(String corporateName) {

        LOG.info(ALPHABETICAL_SEARCH + "Running best match query for: " + corporateName);

        return QueryBuilders.matchPhrasePrefixQuery("items.corporate_name.startsWith", corporateName);
    }

    private QueryBuilder createStartsWithQuery(String corporateName) {

        LOG.info(ALPHABETICAL_SEARCH + "Running starts with query for: " + corporateName);

        return QueryBuilders.matchPhrasePrefixQuery("items.corporate_name.startswith", corporateName);
    }

    private QueryBuilder createAlphabeticalQuery() {

        return QueryBuilders.matchAllQuery();
    }

    private Company getCompany(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> items = (Map<String, Object>) sourceAsMap.get("items");
        Map<String, Object> links = (Map<String, Object>) sourceAsMap.get("links");

        Company company = new Company();
        Items companyItems = new Items();
        Links companyLinks = new Links();

        companyItems.setCorporateName((String) (items.get("corporate_name")));
        companyItems.setCompanyNumber((String) (items.get("company_number")));
        companyItems.setCompanyStatus((String) (items.get("company_status")));

        companyLinks.setSelf((String) (links.get("self")));

        company.setId((String) sourceAsMap.get("ID"));
        company.setCompanyType((String) sourceAsMap.get("company_type"));
        company.setItems(companyItems);
        company.setLinks(companyLinks);

        return company;
    }

    private SearchRequest createBaseSearchRequest(String requestId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environmentReader.getMandatoryString(INDEX));
        searchRequest.preference(requestId);

        return searchRequest;
    }
}
