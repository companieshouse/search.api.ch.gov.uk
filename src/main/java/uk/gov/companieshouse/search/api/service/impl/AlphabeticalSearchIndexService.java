package uk.gov.companieshouse.search.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.company.Company;
import uk.gov.companieshouse.search.api.model.company.Items;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.SearchIndexService;
import uk.gov.companieshouse.search.api.service.SearchRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

@Service
public class AlphabeticalSearchIndexService implements SearchIndexService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private SearchRequestService searchRequestService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseObject search(String corporateName) {

        SearchResults searchResults;

        try {
            searchResults = performAlphabeticalSearch(corporateName);
        } catch (IOException ex) {
            return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
        }

        return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
    }

    private SearchResults performAlphabeticalSearch(String corporateName) throws IOException {

        int indexFrom = 0;

        // initial search for companies
        SearchResponse searchResponse = searchRestClient(corporateName, indexFrom);

        // obtain the highest matched name from aggregations
        String topHitName = getTopHitAggregation(searchResponse.getAggregations().asList());

        return searchIndex(indexFrom, topHitName, searchResponse.getHits(), corporateName);
    }

    private String getTopHitAggregation(List<Aggregation> aggregations) throws IOException {

        // get top hits
        TopHits topHits = (TopHits) aggregations.get(0);

        //get search hits from top hits
        SearchHits searchTopHits = topHits.getHits();

        // extract the top hit from position 0 as we know there is only one
        Optional<Company> companyTopHit =
            Optional.of(new ObjectMapper()
                .readValue(searchTopHits
                    .getAt(0)
                    .getSourceAsString(), Company.class));

        return companyTopHit.map(Company::getItems)
            .map(Items::getCorporateName)
            .orElse("");
    }

    private SearchResults searchIndex(int indexFrom, String topHitName, SearchHits searchHits,
        String corporateName) throws IOException {

        List<Items> companies = getCompaniesFromSearchHits(searchHits);

        Items matchedCompany = companies
            .stream()
            .filter(company -> topHitName.equals(company.getCorporateName()))
            .findFirst()
            .orElse(null);

        if (matchedCompany != null && matchedCompany.getCorporateName().equals(topHitName)) {
            return new SearchResults("alphabetical company search", companies);
        }

        int newIndexFrom = indexFrom + 10;

        SearchResponse searchResponse = searchRestClient(corporateName, newIndexFrom);

        return searchIndex(newIndexFrom, topHitName, searchResponse.getHits(), corporateName);
    }

    private SearchResponse searchRestClient(String corporateName, int searchIndexFrom) throws IOException {
        return client.search(
            searchRequestService.createSearchRequest(corporateName, searchIndexFrom), DEFAULT);
    }

    private List<Items> getCompaniesFromSearchHits(SearchHits searchHits) throws IOException {

        List<Items> companies = new ArrayList<>();

        for(SearchHit searchHit : searchHits.getHits()) {

            Company company =
                new ObjectMapper().readValue(searchHit.getSourceAsString(), Company.class);

            companies.add(company.getItems());
        }

        return companies;
    }
}
