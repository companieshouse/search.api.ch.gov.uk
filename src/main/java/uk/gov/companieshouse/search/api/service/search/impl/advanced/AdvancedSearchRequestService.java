package uk.gov.companieshouse.search.api.service.search.impl.advanced;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogMap;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.search.api.elasticsearch.AdvancedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;

@Service
public class AdvancedSearchRequestService {

    @Autowired
    private AdvancedSearchRequests advancedSearchRequests;

    @Autowired
    private ElasticSearchResponseMapper elasticSearchResponseMapper;


    private static final String RESULT_FOUND = "A result has been found";

    public SearchResults<Company> getSearchResults(AdvancedSearchQueryParams queryParams, String requestId) throws SearchException {

        Map<String, Object> logMap = getLogMap(queryParams, requestId);

        getLogger().info("Getting advanced search results", logMap);
        logMap.remove(MESSAGE);

        String etag = GenerateEtagUtil.generateEtag();
        TopHit topHit = new TopHit();
        List<Company> results = new ArrayList<>();
        String kind = "search#advanced-search";
        long numberOfHits;

        try {
            SearchHits hits = advancedSearchRequests.getCompanies(queryParams, requestId);
            numberOfHits = hits.getTotalHits().value;

            if (hits.getTotalHits().value > 0) {
                getLogger().info(RESULT_FOUND, logMap);

                Company topHitCompany = elasticSearchResponseMapper
                        .mapAdvancedSearchResponse(hits.getHits()[0]);

                topHit = elasticSearchResponseMapper.mapAdvancedTopHit(topHitCompany);

                hits.forEach(h -> results.add(elasticSearchResponseMapper.mapAdvancedSearchResponse(h)));
            }
        } catch (IOException e) {
            getLogger().error("failed to return a company using advanced search", logMap);
            throw new SearchException("error occurred reading data from the search hits", e);
        }
        SearchResults<Company> advancedSearchResults =
            new SearchResults<>(etag, topHit, results, kind);

        advancedSearchResults.setHits(numberOfHits);

        return advancedSearchResults;
    }
}