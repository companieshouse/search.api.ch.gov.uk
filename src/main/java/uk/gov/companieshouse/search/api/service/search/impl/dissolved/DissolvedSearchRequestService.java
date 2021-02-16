package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.search.api.elasticsearch.DissolvedSearchRequests;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.search.SearchRequestService;

@Service
public class DissolvedSearchRequestService implements SearchRequestService {
	
	@Autowired
    private AlphaKeyService alphaKeyService;
	
	@Autowired
	private DissolvedSearchRequests dissolvedSearchRequests;

	@Override
	public SearchResults getAlphabeticalSearchResults(String corporateName, String requestId) throws SearchException {
		Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
		logMap.put(LoggingUtils.COMPANY_NAME, corporateName);
		LoggingUtils.getLogger().info("getting dissolved search results", logMap);
		
		String orderedAlphaKey;
		
		AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(corporateName);
        if (alphaKeyResponse != null) {
        	orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
        }
        
		return null;
	}
	
	
	
	

}
