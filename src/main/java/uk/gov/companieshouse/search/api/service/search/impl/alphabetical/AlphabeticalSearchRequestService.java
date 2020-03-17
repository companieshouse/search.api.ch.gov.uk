package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.query.AlphabeticalSearchQueries;
import uk.gov.companieshouse.search.api.service.rest.RestClientService;
import uk.gov.companieshouse.search.api.service.search.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class AlphabeticalSearchRequestService implements SearchRequestService {

    @Autowired
    private EnvironmentReader environmentReader;
    @Autowired
    private RestClientService searchRestClient;
    @Autowired
    private AlphaKeyService alphaKeyService;
    @Autowired
    private AlphabeticalSearchQueries alphabeticalSearchQueries;

    private static final String INDEX = "ALPHABETICAL_SEARCH_INDEX";
    private static final String RESULTS_SIZE = "ALPHABETICAL_SEARCH_RESULT_MAX";
    private static final String ALPHABETICAL_SEARCH = "Alphabetical Search: ";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults createSearchRequest(String corporateName, String requestId) throws SearchException {

        LOG.info(ALPHABETICAL_SEARCH + "Creating search request for: " + corporateName + " for user with Id: " + requestId);

        String orderedAlphakey = "";
        String topHitCompanyName = "";
        List<Company> results = new ArrayList<>();

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(corporateName);
        if (alphaKeyResponse != null) {
            orderedAlphakey = alphaKeyResponse.getOrderedAlphaKey();
        }

        try {
            SearchResponse searchResponse = getBestMatchResponse(orderedAlphakey, requestId);
            SearchHits hits = searchResponse.getHits();

            if (hits.getTotalHits().value == 0) {

                SearchResponse searchResponseStartsWith = getStartsWithResponse(orderedAlphakey, requestId);
                hits = searchResponseStartsWith.getHits();
            }

            if (hits.getTotalHits().value == 0) {

                SearchResponse searchResponseCorporateName = getCorporateNameStartsWithResponse(orderedAlphakey, requestId);
                hits = searchResponseCorporateName.getHits();
            }

            if (hits.getTotalHits().value > 0) {
                LOG.info("A result has been found");

                SearchHit topHit = hits.getHits()[0];
                String orderedAlphakeyWithId = getOrderedAlphaKeyWithId(hits.getHits()[0]);


                SearchRequest searchAlphabetic = createBaseSearchRequest(requestId);
                Company topHitCompany = getCompany(topHit);
                topHitCompanyName = topHitCompany.getItems().getCorporateName();

                SearchResponse searchResponseAboveResults = getAboveResultsResponse(searchAlphabetic,
                    orderedAlphakeyWithId, topHitCompanyName);

                hits = searchResponseAboveResults.getHits();
                hits.forEach(h -> results.add(getCompany(h)));

                Collections.reverse(results);

                LOG.info("Retrieving the top hit: " + topHitCompanyName);
                results.add(topHitCompany);

                SearchResponse searchResponseAfterAsc = getDescendingResultsResponse(searchAlphabetic,
                    orderedAlphakeyWithId, topHitCompanyName);
                hits = searchResponseAfterAsc.getHits();

                hits.forEach(h -> results.add(getCompany(h)));
            }
        } catch (IOException e) {
            LOG.error(ALPHABETICAL_SEARCH + "failed to map highest map to company object for: " + corporateName, e);
            throw new SearchException("error occurred reading data for highest match from " +
                "searchHits", e);
        }
        return new SearchResults("", topHitCompanyName, results);
    }

    private SearchResponse getBestMatchResponse(String orderedAlphakey, String requestId) throws IOException {
        SearchRequest searchRequestBestMatch = createBaseSearchRequest(requestId);
        searchRequestBestMatch.source(bestMatchSourceBuilder(
            alphabeticalSearchQueries.createOrderedAlphaKeySearchQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        return searchRestClient.searchRestClient(searchRequestBestMatch);
    }

    private SearchResponse getStartsWithResponse(String orderedAlphakey, String requestId) throws IOException {
        LOG.info("A hit was not found for: " + orderedAlphakey + ", falling back to prefix on alphakey");
        SearchRequest searchRequestStartsWith = createBaseSearchRequest(requestId);

        searchRequestStartsWith.source(bestMatchSourceBuilder(
            alphabeticalSearchQueries.createOrderedAlphaKeyKeywordQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        return searchRestClient.searchRestClient(searchRequestStartsWith);
    }

    private SearchResponse getCorporateNameStartsWithResponse(
        String orderedAlphakey,
        String requestId) throws IOException {
        LOG.info("A hit was not found for: " + orderedAlphakey + ", falling back to corporate name");
        SearchRequest searchRequestCorporateName = createBaseSearchRequest(requestId);

        // Consider using corporateName instead of orderedAlphakey
        // Currently using same logic as python application
        searchRequestCorporateName.source(bestMatchSourceBuilder(
            alphabeticalSearchQueries.createStartsWithQuery(orderedAlphakey),
            ORDERED_ALPHA_KEY_WITH_ID, SortOrder.ASC));

        return searchRestClient.searchRestClient(searchRequestCorporateName);
    }

    private SearchResponse getAboveResultsResponse(
        SearchRequest searchAlphabetic,
        String orderedAlphakeyWithId,
        String topHitCompanyName) throws IOException {
        LOG.info("Retrieving the alphabetically descending results for search query: " + topHitCompanyName);
        searchAlphabetic.source(alphabeticalSourceBuilder(orderedAlphakeyWithId,
            alphabeticalSearchQueries.createAlphabeticalQuery(), SortOrder.DESC));
        return searchRestClient.searchRestClient(searchAlphabetic);
    }

    private SearchResponse getDescendingResultsResponse(
        SearchRequest searchAlphabetic,
        String orderedAlphakeyWithId,
        String topHitCompanyName) throws IOException {

        LOG.info("Retrieving the alphabetically ascending results from: " + topHitCompanyName);
        searchAlphabetic.source(alphabeticalSourceBuilder(orderedAlphakeyWithId,
            alphabeticalSearchQueries.createAlphabeticalQuery(), SortOrder.ASC));
        return searchRestClient.searchRestClient(searchAlphabetic);
    }

    private String getOrderedAlphaKeyWithId(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return (String) sourceAsMap.get(ORDERED_ALPHA_KEY_WITH_ID);
    }

    private SearchSourceBuilder bestMatchSourceBuilder(QueryBuilder queryBuilder, String sortField, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.sort(sortField, sortOrder);

        return sourceBuilder;
    }

    private SearchSourceBuilder alphabeticalSourceBuilder(String orderedAlphakeyWithId, QueryBuilder queryBuilder, SortOrder sortOrder) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(Integer.parseInt(environmentReader.getMandatoryString(RESULTS_SIZE)));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.searchAfter(new Object[]{orderedAlphakeyWithId});
        sourceBuilder.sort(ORDERED_ALPHA_KEY_WITH_ID, sortOrder);

        return sourceBuilder;
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
