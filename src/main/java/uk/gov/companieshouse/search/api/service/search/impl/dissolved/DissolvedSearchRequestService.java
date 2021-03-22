package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.search.api.elasticsearch.DissolvedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.DissolvedTopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DissolvedSearchRequestService {

    @Autowired
    private AlphaKeyService alphaKeyService;

    @Autowired
    private DissolvedSearchRequests dissolvedSearchRequests;

    private static final String SEARCH_RESULTS_KIND = "searchresults#dissolvedCompany";
    private static final String TOP_KIND = "search#alphabeticalDissolved";
    private static final int FALLBACK_QUERY_LIMIT = 20;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    public DissolvedSearchResults getSearchResults(String companyName, String requestId) throws SearchException {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logMap.put(LoggingUtils.COMPANY_NAME, companyName);
        logMap.put(LoggingUtils.INDEX, LoggingUtils.INDEX_DISSOLVED);
        LoggingUtils.getLogger().info("getting dissolved search results", logMap);

        String orderedAlphaKey = "";
        List<DissolvedCompany> results = new ArrayList<>();
        DissolvedTopHit topHit = new DissolvedTopHit();
        String etag = GenerateEtagUtil.generateEtag();
        String kind = TOP_KIND;
        boolean isFallbackQuery = false;

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(companyName);
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
        }

        try {
            SearchHits hits = getSearchHits(orderedAlphaKey, requestId);

            if (hits.getTotalHits().value == 0) {
                LoggingUtils.getLogger().info("A result was not found, reducing search term to find result", logMap);

                hits = peelbackSearchRequest(hits, orderedAlphaKey, requestId);
            }

            if (hits.getTotalHits().value > 0) {
                LoggingUtils.getLogger().info("A result has been found", logMap);

                String orderedAlphaKeyWithId;
                SearchHit bestMatch;

                orderedAlphaKeyWithId = SearchRequestUtils.getOrderedAlphaKeyWithId(hits.getHits()[0]);
                bestMatch = hits.getHits()[0];

                DissolvedCompany topHitCompany = mapESResponse(bestMatch);

                mapTopHit(topHit, topHitCompany);

                populateSearchResults(requestId, topHit.getCompanyName(), results, topHitCompany,
                    orderedAlphaKeyWithId);
            }
        } catch (IOException e) {
            LoggingUtils.getLogger().error("failed to map highest map to company object",
                logMap);
            throw new SearchException("error occurred reading data for highest match from " + "searchHits", e);
        }

        return new DissolvedSearchResults(etag, topHit, results, kind);
    }

    public DissolvedSearchResults getBestMatchSearchResults(String companyName, String requestId) throws SearchException {

        String etag = GenerateEtagUtil.generateEtag();
        DissolvedTopHit topHit = new DissolvedTopHit();
        List<DissolvedCompany> results = new ArrayList<>();

        return new DissolvedSearchResults(etag, topHit, results, "search#dissolved");
    }

    private DissolvedCompany mapESResponse(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        Map<String, Object> address = (Map<String, Object>) sourceAsMap.get("address");
        List<Object> previousCompanyNamesList = (List<Object>) sourceAsMap.get("previous_company_names");
        DissolvedCompany dissolvedCompany = new DissolvedCompany();
        if(previousCompanyNamesList != null) {
            List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
            for(Object o : previousCompanyNamesList){
                Map<String, Object> companyNames = (Map<String, Object>) o;
                PreviousCompanyName companyName = new PreviousCompanyName();
                companyName.setName((String) companyNames.get("name"));
                companyName.setDateOfNameCessation(LocalDate.parse((String) companyNames.get("ceased_on"),formatter));
                companyName.setDateOfNameEffectiveness(LocalDate.parse((String) companyNames.get("effective_from"),formatter));
                previousCompanyNames.add(companyName);
            }
            dissolvedCompany.setPreviousCompanyNames(previousCompanyNames);
        }

        Address roAddress = new Address();

        dissolvedCompany.setCompanyName((String) sourceAsMap.get("company_name"));
        dissolvedCompany.setCompanyNumber((String) sourceAsMap.get("company_number"));
        dissolvedCompany.setCompanyStatus((String) sourceAsMap.get("company_status"));
        dissolvedCompany.setKind(SEARCH_RESULTS_KIND);
        dissolvedCompany.setDateOfCessation(LocalDate.parse((String) sourceAsMap.get("date_of_cessation"), formatter));
        dissolvedCompany.setDateOfCreation(LocalDate.parse((String) sourceAsMap.get("date_of_creation"), formatter));
        if(address != null && address.containsKey("locality")) {
            roAddress.setLocality((String) address.get("locality"));
        }
        if(address != null && address.containsKey("postal_code")) {
            roAddress.setPostalCode((String) address.get("postal_code"));
        }

        dissolvedCompany.setAddress(roAddress);

        return dissolvedCompany;
    }

    private void mapTopHit(DissolvedTopHit topHit, DissolvedCompany dissolvedCompany) {
        topHit.setCompanyName(dissolvedCompany.getCompanyName());
        topHit.setCompanyNumber(dissolvedCompany.getCompanyNumber());
        topHit.setCompanyStatus(dissolvedCompany.getCompanyStatus());
        topHit.setKind(dissolvedCompany.getKind());
        topHit.setAddress(dissolvedCompany.getAddress());
        topHit.setDateOfCessation(dissolvedCompany.getDateOfCessation());
        topHit.setDateOfCreation(dissolvedCompany.getDateOfCreation());

        if (dissolvedCompany.getPreviousCompanyNames() != null) {
            topHit.setPreviousCompanyNames(dissolvedCompany.getPreviousCompanyNames());
        }
    }

    private SearchHits getSearchHits(String orderedAlphakey, String requestId) throws IOException {
        SearchHits hits =  dissolvedSearchRequests
                .getBestMatchResponse(orderedAlphakey, requestId);

        if (hits.getTotalHits().value == 0) {
            hits = dissolvedSearchRequests
                    .getStartsWithResponse(orderedAlphakey, requestId);
        }

        if (hits.getTotalHits().value == 0) {
            hits = dissolvedSearchRequests
                    .getCorporateNameStartsWithResponse(orderedAlphakey, requestId);
        }
        return hits;
    }

    public SearchHits peelbackSearchRequest(SearchHits hits, String orderedAlphaKey,
                                            String requestId) throws IOException {
        for (int i = 0; i < orderedAlphaKey.length(); i++) {

            if (hits.getTotalHits().value > 0 || i == FALLBACK_QUERY_LIMIT) {
                break;
            }

            if (i != orderedAlphaKey.length() - 1) {
                String resultString = orderedAlphaKey.substring(0, orderedAlphaKey.length() - i);
                hits = getSearchHits(resultString, requestId);
            }
        }
        return hits;
    }

    private void populateSearchResults(String requestId,
                                       String topHitCompanyName,
                                       List<DissolvedCompany> results,
                                       DissolvedCompany topHitCompany,
                                       String orderedAlphaKeyWithId) throws IOException {
        SearchHits hits;
        hits = dissolvedSearchRequests.getAboveResultsResponse(requestId, orderedAlphaKeyWithId, topHitCompanyName);
        hits.forEach(h -> results.add(mapESResponse(h)));

        Collections.reverse(results);

        LoggingUtils.getLogger().info("Retrieving the top hit: " + topHitCompanyName);
        results.add(topHitCompany);

        hits = dissolvedSearchRequests.getDescendingResultsResponse(requestId, orderedAlphaKeyWithId,
            topHitCompanyName);

        hits.forEach(h -> results.add(mapESResponse(h)));
    }
}
