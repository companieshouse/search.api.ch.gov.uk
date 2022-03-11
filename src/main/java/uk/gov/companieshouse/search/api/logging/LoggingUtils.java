package uk.gov.companieshouse.search.api.logging;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

import java.util.HashMap;
import java.util.Map;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;

public class LoggingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    public static final String COMPANY_NAME = "company_name_includes";
    public static final String COMPANY_NUMBER = "company_number";
    public static final String COMPANY_STATUS = "company_status";
    public static final String COMPANY_TYPE = "company_type";
    public static final String COMPANY_SUBTYPE = "company_subtype";
    public static final String DISSOLVED_SEARCH_ALPHABETICAL = "dissolved - alphabetical";
    public static final String INDEX = "index_name";
    public static final String INDEX_ALPHABETICAL = "alphabetical_search_index";
    public static final String INDEX_DISSOLVED = "dissolved_search_index";
    public static final String ADVANCED_SEARCH_INDEX = "advanced_search_index";
    public static final String MESSAGE = "message";
    public static final String ORDERED_ALPHAKEY = "ordered_alphakey";
    public static final String SAME_AS_ALPHAKEYKEY = "same_as_alphakey";
    public static final String ORDERED_ALPHAKEY_WITH_ID = "ordered_alphakey_with_id";
    public static final String REQUEST_ID = "request_id";
    public static final String UPSERT_COMPANY_NUMBER = "upsert_company_number";
    public static final String SEARCH_AFTER = "search_after";
    public static final String SEARCH_BEFORE = "search_before";
    public static final String SEARCH_TYPE = "search_type";
    public static final String SIZE = "size";
    public static final String START_INDEX = "start_index";
    public static final String LOCATION = "location";
    public static final String SUCCESSFUL_SEARCH = "Advanced search successful";
    public static final String STANDARD_ERROR_MESSAGE = "An error occurred while advanced searching for a company";
    public static final String NO_RESULTS_FOUND = "No results were returned while advanced searching for a company";
    public static final String INCORPORATED_FROM = "incorporated_from";
    public static final String INCORPORATED_TO = "incorporated_to";
    public static final String DISSOLVED_FROM = "dissolved_from";
    public static final String DISSOLVED_TO = "dissolved_to";
    public static final String SIC_CODES = "sic_codes";
    public static final String COMPANY_NAME_EXCLUDES = "company_name_excludes";

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

    public static Map<String, Object> getLogMap(AdvancedSearchQueryParams queryParams, String requestId) {
        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logIfNotNull(logMap, START_INDEX, queryParams.getStartIndex());
        logIfNotNull(logMap, COMPANY_NAME, queryParams.getCompanyNameIncludes());
        logIfNotNull(logMap, LOCATION, queryParams.getLocation());
        logIfNotNull(logMap, INCORPORATED_FROM, queryParams.getIncorporatedFrom());
        logIfNotNull(logMap, INCORPORATED_TO, queryParams.getIncorporatedTo());
        logIfNotNull(logMap, COMPANY_STATUS, queryParams.getCompanyStatusList());
        logIfNotNull(logMap, SIC_CODES, queryParams.getSicCodes());
        logIfNotNull(logMap, COMPANY_TYPE, queryParams.getCompanyTypeList());
        logIfNotNull(logMap, COMPANY_SUBTYPE, queryParams.getCompanySubtypeList());
        logIfNotNull(logMap, DISSOLVED_FROM, queryParams.getDissolvedFrom());
        logIfNotNull(logMap, DISSOLVED_TO, queryParams.getDissolvedTo());
        logIfNotNull(logMap, COMPANY_NAME_EXCLUDES, queryParams.getCompanyNameExcludes());
        logMap.put(INDEX, LoggingUtils.ADVANCED_SEARCH_INDEX);
        getLogger().info("advanced search filters", logMap);

        return logMap;
    }
}
