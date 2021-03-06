package uk.gov.companieshouse.search.api.logging;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

import java.util.HashMap;
import java.util.Map;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class LoggingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    public static final String ALPHABETICAL_SEARCH = "alphabetical";
    public static final String COMPANY_NAME = "company_name";
    public static final String COMPANY_NUMBER = "company_number";
    public static final String DISSOLVED_SEARCH_ALPHABETICAL = "dissolved - alphabetical";
    public static final String DISSOLVED_SEARCH_BEST_MATCH = "dissolved - best match";
    public static final String DISSOLVED_SEARCH_PREVIOUS_NAMES_BEST_MATCH = "dissolved - previous names best match";
    public static final String INDEX = "index_name";
    public static final String INDEX_ALPHABETICAL = "alphabetical_search_index";
    public static final String INDEX_DISSOLVED = "dissolved_search_index";
    public static final String ENHANCED_SEARCH_INDEX = "enhanced_search_index";
    public static final String MESSAGE = "message";
    public static final String ORDERED_ALPHAKEY = "ordered_alphakey";
    public static final String ORDERED_ALPHAKEY_WITH_ID = "ordered_alphakey_with_id";
    public static final String REQUEST_ID = "request_id";
    public static final String UPSERT_COMPANY_NUMBER = "upsert_company_number";
    public static final String SEARCH_AFTER = "search_after";
    public static final String SEARCH_BEFORE = "search_before";
    public static final String SEARCH_TYPE = "search_type";
    public static final String SIZE = "size";
    public static final String START_INDEX = "start_index";

    public static final String REQUEST_ID_LOG_KEY = "request_id";
    public static final String STATUS_LOG_KEY = "status";
    public static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    
    private LoggingUtils() throws IllegalAccessException {
        throw new IllegalAccessException("LoggingUtils is not to be instantiated");
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Map<String, Object> createLoggingMap(String requestId) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(REQUEST_ID, requestId);
        return logMap;
    }
    
    public static void logIfNotNull(Map<String, Object> logMap, String key, Object value) {
        if(value != null) {
            logMap.put(key, value);
        }
    }

}
