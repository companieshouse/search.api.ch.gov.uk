package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISSOLVED_SEARCH;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_TYPE;

import java.util.Map;

import org.springframework.stereotype.Service;

import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

@Service
public class DissolvedSearchIndexService {
	
    public DissolvedResponseObject search(String companyName, String requestId) {
    	Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
    	logMap.put(COMPANY_NAME, companyName);
    	logMap.put(SEARCH_TYPE, DISSOLVED_SEARCH);
    	LoggingUtils.getLogger().info("searching for company", logMap);
    	
    	DissolvedSearchResults searchResults;
        
        LoggingUtils.getLogger().info("successful search for dissolved company", logMap);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
    }
}
