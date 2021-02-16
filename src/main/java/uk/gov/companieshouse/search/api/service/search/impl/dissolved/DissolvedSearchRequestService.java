package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.search.api.elasticsearch.DissolvedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Items;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Links;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;

@Service
public class DissolvedSearchRequestService {
	
	@Autowired
    private AlphaKeyService alphaKeyService;
	
	@Autowired
	private DissolvedSearchRequests dissolvedSearchRequests;

	private static final String DISSOLVED_SEARCH = "Dissolved Search: ";

	public DissolvedSearchResults getSearchResults(String companyName, String requestId) throws SearchException {
		Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
		logMap.put(LoggingUtils.COMPANY_NAME, companyName);
		LoggingUtils.getLogger().info("getting dissolved search results", logMap);
		
		String orderedAlphaKey = "";
		List<DissolvedCompany> results = new ArrayList<>();
		TopHit topHit = new TopHit();
		
		AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(companyName);
        if (alphaKeyResponse != null) {
        	orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
        }

		try {
			SearchHits hits = dissolvedSearchRequests
					.getBestMatchResponse(orderedAlphaKey, requestId);
			if (hits.getTotalHits().value == 0) {

				hits = dissolvedSearchRequests
						.getStartsWithResponse(orderedAlphaKey, requestId);
			}

			if (hits.getTotalHits().value == 0) {

				hits = dissolvedSearchRequests
						.getCorporateNameStartsWithResponse(orderedAlphaKey, requestId);
			}

			if (hits.getTotalHits().value > 0) {
				LoggingUtils.getLogger().info("A result has been found", logMap);

				String orderedAlphaKeyWithId = SearchRequestUtils.getOrderedAlphaKeyWithId(hits.getHits()[0]);
				SearchHit bestMatch = hits.getHits()[0];
				DissolvedCompany topHitCompany = mapESResponse(bestMatch);
				topHit.setCompanyName(topHitCompany.getCompanyName());
				topHit.setCompanyNumber(topHitCompany.getCompanyNumber());

				populateSearchResults(requestId, topHit.getCompanyName(), results, topHitCompany, orderedAlphaKeyWithId);
			}
		} catch (IOException e) {
			LoggingUtils.getLogger().error(DISSOLVED_SEARCH + "failed to map highest map to company object for: ", logMap);
			throw new SearchException("error occurred reading data for highest match from " +
					"searchHits", e);
		}

		return new DissolvedSearchResults("", topHit, results);
	}

	private DissolvedCompany mapESResponse(SearchHit hit) {
		Map<String, Object> sourceAsMap = hit.getSourceAsMap();
		Map<String, Object> address = (Map<String, Object>) sourceAsMap.get("address");
		List<PreviousCompanyName> previousCompanyNamesList = (List<PreviousCompanyName>) sourceAsMap.get("previous_company_names");

		DissolvedCompany dissolvedCompany = new DissolvedCompany();
		Address roAddress = new Address();

		dissolvedCompany.setCompanyName((String) sourceAsMap.get("company_name"));
		dissolvedCompany.setCompanyNumber((String) sourceAsMap.get("company_number"));
		dissolvedCompany.setCompanyStatus((String) sourceAsMap.get("company_status"));
		dissolvedCompany.setDateOfCessation((String) sourceAsMap.get("date_of_cessation"));
		dissolvedCompany.setDateOfCreation((String) sourceAsMap.get("date_of_creation"));
		roAddress.setLocality((String) address.get("locality"));
		roAddress.setPostalCode((String) address.get("postal_code"));

		dissolvedCompany.setAddress(roAddress);
		dissolvedCompany.setPreviousCompanyNames(previousCompanyNamesList);

		return dissolvedCompany;
	}

	private void populateSearchResults(String requestId,
									   String topHitCompanyName,
									   List<DissolvedCompany> results,
									   DissolvedCompany topHitCompany,
									   String orderedAlphaKeyWithId) throws IOException {
		SearchHits hits;
		hits = dissolvedSearchRequests.getAboveResultsResponse(requestId, orderedAlphaKeyWithId,
				topHitCompanyName);
		hits.forEach(h -> results.add(mapESResponse(h)));

		Collections.reverse(results);

		LoggingUtils.getLogger().info("Retrieving the top hit: " + topHitCompanyName);
		results.add(topHitCompany);

		hits = dissolvedSearchRequests.getDescendingResultsResponse(requestId, orderedAlphaKeyWithId,
				topHitCompanyName);

		hits.forEach(h -> results.add(mapESResponse(h)));
	}

}
