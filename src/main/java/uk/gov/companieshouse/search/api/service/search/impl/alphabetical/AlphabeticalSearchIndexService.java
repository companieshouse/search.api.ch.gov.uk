package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.exception.ObjectMapperException;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;
import uk.gov.companieshouse.search.api.service.search.SearchRestClientService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class AlphabeticalSearchIndexService implements SearchIndexService {

    @Autowired
    private SearchRestClientService searchRestClient;

    @Autowired
    private SearchRequestService searchRequestService;

    private static final String HIGHEST_MATCH = "highest_match";

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private static final String ALPHABETICAL_SEARCH = "Alphabetical Search: ";

    private static final String SEARCH_TYPE = "alphabetical_search";

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseObject search(String corporateName) {

        SearchResults searchResults;

        try {
            LOG.info(ALPHABETICAL_SEARCH + "started for: " + corporateName);
            searchResults = performAlphabeticalSearch(corporateName);
        } catch (SearchException | ObjectMapperException e) {
            LOG.error("An error occurred in alphabetical search whilst searching: " + corporateName, e);
            return new ResponseObject(ResponseStatus.SEARCH_ERROR, null);
        }

        if(searchResults.getSearchResults() != null) {
            LOG.info(ALPHABETICAL_SEARCH + "successful for: " + corporateName);
            return new ResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
        }

        LOG.info(ALPHABETICAL_SEARCH + "No results were returned while searching: " + corporateName);
        return new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND, null);
    }

    private SearchResults performAlphabeticalSearch(String corporateName)
        throws SearchException, ObjectMapperException {

        SearchResponse searchResponse;

        try {
            searchResponse = searchRestClient.searchRestClient(
                searchRequestService.createSearchRequest(corporateName));
        } catch (IOException e) {
            LOG.error(ALPHABETICAL_SEARCH + "Failed to get a search response from elastic search " +
                "for: " + corporateName, e);
            throw new SearchException("Error occurred while searching index", e);
        }

        String highestMatchName = null;
        if(searchResponse != null && searchResponse.getAggregations() != null) {
            highestMatchName = getAggregatedSearchResults(
                searchResponse.getAggregations().asList(), corporateName);
        }

        if(highestMatchName != null) {
            return getSearchResults(highestMatchName, searchResponse.getHits(), corporateName);
        } else {
            LOG.info(ALPHABETICAL_SEARCH + "Could not locate a highest match in the search " +
                "aggregation for: " + corporateName);
            throw new SearchException("highest match was not located in the search, unable to " +
                "process search request");
        }
    }

    private String getAggregatedSearchResults(List<Aggregation> aggregations, String corporateName)
        throws ObjectMapperException {

        // loop the aggregations to obtain the highest match.
        for (Aggregation aggregation : aggregations) {

            if (aggregation.getName().equals(HIGHEST_MATCH)) {
                return getHighestMatchedCompanyName(aggregation, corporateName);
            }
        }
        return null;
    }

    private SearchResults<Company> getSearchResults(String highestMatchName, SearchHits searchHits,
        String corporateName)
        throws ObjectMapperException {

        SearchResults<Company> searchResults = new SearchResults();

        int highestMatchIndexPos = 0;
        List<Company> companies = getCompaniesFromSearchHits(searchHits, corporateName);

        // find the pos in the list of companies where the highest match is.
        for(Company company : companies) {
            if (company.getItems().getCorporateName().equals(highestMatchName)) {
                searchResults = getAlphabeticalSearchResults(companies,
                    highestMatchIndexPos);
            }
            highestMatchIndexPos++;
        }

        searchResults.setTopHit(highestMatchName);

        return searchResults;
    }

    private SearchResults<Company> getAlphabeticalSearchResults(List<Company> companies,
        int highestMatchIndexPos) {

        List<Company> searchCompanyResults = new ArrayList<>();
        SearchResults<Company> searchResults = new SearchResults<>();

        int totalResults = companies.size();

        int startIndex = getIndexStart(highestMatchIndexPos);
        int endIndex = getIndexEnd(totalResults, highestMatchIndexPos);

        // loop to get 20 hits with 9 records above and 10 below the highest match.
        for(int i = startIndex; i < endIndex + 1; i++) {
            searchCompanyResults.add(companies.get(i));
        }

        searchResults.setSearchType(SEARCH_TYPE);
        searchResults.setSearchResults(searchCompanyResults);

        return searchResults;
    }

    private int getIndexEnd(int totalResults, int highestMatchIndexPos) {

        int indexEndPos = highestMatchIndexPos + 10;
        int differenceIndexPos = indexEndPos - totalResults;

        if (differenceIndexPos < 0) {
            return indexEndPos;
        } else {
            return indexEndPos - differenceIndexPos;
        }
    }

    private int getIndexStart(int highestMatchIndexPos) {

        int indexStartPos = highestMatchIndexPos - 9;

        if (indexStartPos >= 0) {
            return indexStartPos;
        } else {
            return 0;
        }
    }

    private String getHighestMatchedCompanyName(Aggregation aggregation,
        String corporateName) throws ObjectMapperException {

        SearchHits searchHitsHighestMatched = transformToSearchHits(aggregation);

        Optional<Company> companyTopHit;

        try {
            // extract the highest matched name from position 0 as we know there is only one.
            companyTopHit = Optional.of(new ObjectMapper()
                .readValue(searchHitsHighestMatched
                    .getAt(0)
                    .getSourceAsString(), Company.class));

        } catch (IOException e) {
            LOG.error(ALPHABETICAL_SEARCH + "failed to map highest map to company object for: " + corporateName, e);
            throw new ObjectMapperException("error occurred reading data for highest match from " +
                "searchHits", e);
        }

        // return the name of highest match.
        return companyTopHit.map(Company::getItems)
            .map(Items::getCorporateName)
            .orElse("");
    }

    private SearchHits transformToSearchHits(Aggregation aggregation) {

        TopHits topHits = (TopHits) aggregation;
        return topHits.getHits();
    }


    private List<Company> getCompaniesFromSearchHits(SearchHits searchHits,
        String corporateName) throws ObjectMapperException {

        List<Company> companies = new ArrayList<>();

        // loop and map companies from search hits to Company model
        for(SearchHit searchHit : searchHits.getHits()) {

            Company company;

            try {
                company = new ObjectMapper()
                    .readValue(searchHit.getSourceAsString(), Company.class);
            } catch (IOException e) {
                LOG.error(ALPHABETICAL_SEARCH + "failed to map search hit to company object for: " + corporateName, e);
                throw new ObjectMapperException("error occurred reading data for company from " +
                    "searchHits", e);
            }

            companies.add(company);
        }

        // order the list in a natural order using Company compareTo
        return  companies.stream()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());
    }
}

