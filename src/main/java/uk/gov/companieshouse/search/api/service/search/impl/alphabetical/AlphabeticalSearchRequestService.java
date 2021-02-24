package uk.gov.companieshouse.search.api.service.search.impl.alphabetical;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
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
    private AlphaKeyService alphaKeyService;
    @Autowired
    private AlphabeticalSearchRequests alphabeticalSearchRequests;

    private static final String ALPHABETICAL_SEARCH = "Alphabetical Search: ";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults getAlphabeticalSearchResults(String corporateName, String requestId) throws SearchException {

        LOG.info(ALPHABETICAL_SEARCH + "Creating search request for: " + corporateName + " for user with Id: " + requestId);

        String orderedAlphakey = "";
        String topHitCompanyName = "";
        List<Company> results = new ArrayList<>();

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(corporateName);
        if (alphaKeyResponse != null) {
            orderedAlphakey = alphaKeyResponse.getOrderedAlphaKey();
        }

        try {
            SearchHits hits =  alphabeticalSearchRequests
                .getBestMatchResponse(orderedAlphakey, requestId);

            if (hits.getTotalHits().value == 0) {

                hits = alphabeticalSearchRequests
                    .getStartsWithResponse(orderedAlphakey, requestId);
            }

            if (hits.getTotalHits().value == 0) {

                hits = alphabeticalSearchRequests
                    .getCorporateNameStartsWithResponse(orderedAlphakey, requestId);
            }

            if (hits.getTotalHits().value == 0) {

                hits = alphabeticalSearchRequests
                        .noResultsFallbackQuery(orderedAlphakey, requestId);
            }

            if (hits.getTotalHits().value > 0) {
                LOG.info("A result has been found");

                String orderedAlphakeyWithId = getOrderedAlphaKeyWithId(hits.getHits()[0]);
                SearchHit topHit = hits.getHits()[0];
                Company topHitCompany = getCompany(topHit);
                topHitCompanyName = topHitCompany.getItems().getCorporateName();

                populateSearchResults(requestId, topHitCompanyName, results, topHitCompany, orderedAlphakeyWithId);
            }
        } catch (IOException e) {
            LOG.error(ALPHABETICAL_SEARCH + "failed to map highest map to company object for: " + corporateName, e);
            throw new SearchException("error occurred reading data for highest match from " +
                "searchHits", e);
        }
        return new SearchResults("", topHitCompanyName, results);
    }

    private void populateSearchResults(String requestId,
                                       String topHitCompanyName,
                                       List<Company> results,
                                       Company topHitCompany,
                                       String orderedAlphakeyWithId) throws IOException {
        SearchHits hits;
        hits = alphabeticalSearchRequests.getAboveResultsResponse(requestId, orderedAlphakeyWithId,
            topHitCompanyName);
        hits.forEach(h -> results.add(getCompany(h)));

        Collections.reverse(results);

        LOG.info("Retrieving the top hit: " + topHitCompanyName);
        results.add(topHitCompany);

        hits = alphabeticalSearchRequests.getDescendingResultsResponse(requestId, orderedAlphakeyWithId,
            topHitCompanyName);

        hits.forEach(h -> results.add(getCompany(h)));
    }

    private String getOrderedAlphaKeyWithId(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return (String) sourceAsMap.get(ORDERED_ALPHA_KEY_WITH_ID);
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
}
