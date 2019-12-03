package uk.gov.companieshouse.search.api.service.search.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.exception.ObjectMapperException;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

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
        } catch (SearchException | ObjectMapperException ex) {
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if(searchResults.getSearchResults() != null) {
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    private SearchResults performAlphabeticalSearch(String corporateName)
        throws SearchException, ObjectMapperException {

        SearchResponse searchResponse = searchRestClient(corporateName);

        String highestMatchName = getAggregatedSearchResults(searchResponse.getAggregations().asList());

        if(highestMatchName != null) {
            return getSearchResults(highestMatchName, searchResponse.getHits());
        } else {
            throw new SearchException("highest match was not located in the search, unable to " +
                "process search request");
        }
    }

    private SearchResponse searchRestClient(String corporateName) throws SearchException {

        try {
            return client.search(
                searchRequestService.createSearchRequest(corporateName), DEFAULT);
        } catch (IOException e) {
           throw new SearchException("Error occurred while searching index", e);
        }
    }

    private String getAggregatedSearchResults(List<Aggregation> aggregations)
        throws ObjectMapperException {

        // loop the aggregations to obtain the highest match and top hits
        for (Aggregation aggregation : aggregations) {

            if (aggregation.getName().equals(HIGHEST_MATCH)) {
                return getHighestMatchedCompanyName(aggregation);
            }
        }
        return null;
    }

    private SearchResults getSearchResults(String highestMatchName, SearchHits searchHits)
        throws ObjectMapperException {

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

    private String getHighestMatchedCompanyName(Aggregation aggregation) throws ObjectMapperException {

        SearchHits searchHitsHighestMatched = transformToSearchHits(aggregation);

        Optional<Company> companyTopHit;

        try {
            // extract the highest matched company name from position 0 as we know there is only one
            companyTopHit = Optional.of(new ObjectMapper()
                .readValue(searchHitsHighestMatched
                    .getAt(0)
                    .getSourceAsString(), Company.class));

        } catch (IOException e) {
            throw new ObjectMapperException("error occurred reading data for highest match from " +
                "searchHits", e);
        }

        // return the corporate name of highest match
        return companyTopHit.map(Company::getItems)
            .map(Items::getCorporateName)
            .orElse("");
    }

    private SearchHits transformToSearchHits(Aggregation aggregation) {

        TopHits topHits = (TopHits) aggregation;
        return topHits.getHits();
    }


    private List<Items> getCompaniesFromSearchHits(SearchHits searchHits) throws ObjectMapperException {

        List<Items> companies = new ArrayList<>();

        // loop and map companies from search hits
        for(SearchHit searchHit : searchHits.getHits()) {

            Company company;

            try {
                company = new ObjectMapper()
                    .readValue(searchHit.getSourceAsString(), Company.class);
            } catch (IOException e) {
                throw new ObjectMapperException("error occurred reading data for company from " +
                    "searchHits", e);
            }

            companies.add(company.getItems());
        }

        return  companies.stream()
            .sorted(Comparator.comparing(Items::getCorporateName, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
    }
}

