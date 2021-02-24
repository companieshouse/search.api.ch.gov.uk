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
    public static final String DISSOLVED_SEARCH = "dissolved";
    public static final String INDEX = "index_name";
    public static final String INDEX_ALPHABETICAL = "alphabetical_search_index";
    public static final String INDEX_DISSOLVED = "dissolved_search_index";
    public static final String ORDERED_ALPHAKEY = "ordered_alphakey";
    public static final String ORDERED_ALPHAKEY_WITH_ID = "ordered_alphakey_with_id";
    public static final String REQUEST_ID = "request_id";
    public static final String SEARCH_TYPE = "search_type";
    
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

}
