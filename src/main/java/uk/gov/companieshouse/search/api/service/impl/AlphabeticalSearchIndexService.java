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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

@Service
public class AlphabeticalSearchIndexService implements SearchIndexService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private SearchRequestService searchRequestService;

    private static final String HIGHEST_MATCH = "highest_match";

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseObject search(String corporateName) {

        SearchResults searchResults;

        try {
            searchResults = performAlphabeticalSearch(corporateName);
        } catch (IOException ex) {
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if(searchResults.getSearchResults() != null) {
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    private SearchResults performAlphabeticalSearch(String corporateName) throws IOException {

        SearchResponse searchResponse = searchRestClient(corporateName);

        String highestMatchName = getAggregatedSearchResults(searchResponse.getAggregations().asList());

        if(highestMatchName != null) {
            return getSearchResults(highestMatchName, searchResponse.getHits());
        } else {
            // TODO throw a searchException
            return null;
        }
    }

    private SearchResponse searchRestClient(String corporateName) throws IOException {

        // TODO wrap try/catch throw a searchException
        return client.search(
            searchRequestService.createSearchRequest(corporateName), DEFAULT);
    }

    private String getAggregatedSearchResults(List<Aggregation> aggregations) throws IOException {

        // loop the aggregations to obtain the highest match and top hits
        for (Aggregation aggregation : aggregations) {

            if (aggregation.getName().equals(HIGHEST_MATCH)) {
                return getHighestMatchedCompanyName(aggregation);
            }
        }
        return null;
    }

    private SearchResults getSearchResults(String highestMatchName, SearchHits searchHits) throws IOException {
        SearchResults<Items> searchResults = new SearchResults();

        int highestMatchIndexPos = 0;
        List<Items> companies = getCompaniesFromSearchHits(searchHits);

        // find the pos in list that highest match is
        for(Items company : companies) {
            if (company.getCorporateName().equals(highestMatchName)) {
                searchResults = getAlphabeticalSearchResults(companies,
                    highestMatchIndexPos);
            }
            highestMatchIndexPos++;
        }

        return searchResults;
    }

    private SearchResults<Items> getAlphabeticalSearchResults(List<Items> companies, int highestMatchIndexPos) {

        List<Items> searchCompanyResults = new ArrayList<>();
        SearchResults<Items> searchResults = new SearchResults<>();

        int totalResults = companies.size();

        int indexAbove = getIndexForAbove(highestMatchIndexPos);
        int indexBelow = getIndexForBelow(totalResults, highestMatchIndexPos);

        // get 20 hits with potential 9 above and 10 below highest match
        for(int i = indexAbove; i < indexBelow; i++) {
            searchCompanyResults.add(companies.get(i));
        }

        searchResults.setSearchType("alphabetical search");
        searchResults.setSearchResults(searchCompanyResults);

        return searchResults;
    }

    private int getIndexForBelow(int totalResults, int highestMatchIndexPos) {

        int endIndexPosCalc = highestMatchIndexPos + 10;
        int differenceIndexPos = endIndexPosCalc - totalResults;

        if (differenceIndexPos < 0) {
            return endIndexPosCalc;
        } else {
            return endIndexPosCalc - differenceIndexPos;
        }
    }

    private int getIndexForAbove(int highestMatchIndexPos) {

        int topIndexPosCalc = highestMatchIndexPos - 9;

        if (topIndexPosCalc >= 0) {
            return topIndexPosCalc;
        } else {
            return 0;
        }
    }

    private String getHighestMatchedCompanyName(Aggregation aggregation) throws IOException {

        SearchHits searchHitsHighestMatched = transformToSearchHits(aggregation);

        // TODO wrap try/catch throw a ObjectMapperException
        // extract the highest matched company name from position 0 as we know there is only one
        Optional<Company> companyTopHit =
            Optional.of(new ObjectMapper()
                .readValue(searchHitsHighestMatched
                    .getAt(0)
                    .getSourceAsString(), Company.class));

        // return the corporate name of highest match
        return companyTopHit.map(Company::getItems)
            .map(Items::getCorporateName)
            .orElse("");
    }

    private SearchHits transformToSearchHits(Aggregation aggregation) {

        TopHits topHits = (TopHits) aggregation;
        return topHits.getHits();
    }


    private List<Items> getCompaniesFromSearchHits(SearchHits searchHits) throws IOException {

        List<Items> companies = new ArrayList<>();

        // loop and map companies from search hits
        for(SearchHit searchHit : searchHits.getHits()) {

            // TODO wrap try/catch throw a ObjectMapperException
            Company company =
                new ObjectMapper().readValue(searchHit.getSourceAsString(), Company.class);

            companies.add(company.getItems());
        }

        return  companies.stream()
            .sorted(Comparator.comparing(Items::getCorporateName))
            .collect(Collectors.toList());
    }
}

