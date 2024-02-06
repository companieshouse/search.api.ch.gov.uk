package uk.gov.companieshouse.search.api.service.search.impl.advanced;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getAdvancedSearchLogMap;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.search.api.elasticsearch.AdvancedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.mapper.ElasticSearchResponseMapper;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class AdvancedSearchRequestService {

    private final AdvancedSearchRequests advancedSearchRequests;
    private final ElasticSearchResponseMapper elasticSearchResponseMapper;
    private final ConfiguredIndexNamesProvider indices;

    private static final String RESULT_FOUND = "A result has been found";

    public AdvancedSearchRequestService(AdvancedSearchRequests advancedSearchRequests,
        ElasticSearchResponseMapper elasticSearchResponseMapper,
        ConfiguredIndexNamesProvider indices) {
        this.advancedSearchRequests = advancedSearchRequests;
        this.elasticSearchResponseMapper = elasticSearchResponseMapper;
        this.indices = indices;
    }

    public SearchResults<Company> getSearchResults(AdvancedSearchQueryParams queryParams, String requestId) throws SearchException {

        Map<String, Object> logMap = getAdvancedSearchLogMap(queryParams, requestId, indices);

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