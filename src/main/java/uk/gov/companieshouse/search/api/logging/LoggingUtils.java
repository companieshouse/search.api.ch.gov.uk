package uk.gov.companieshouse.search.api.logging;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

public class LoggingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    public static final String COMPANY_NAME = "company_name_includes";
    public static final String COMPANY_NUMBER = "company_number";
    public static final String COMPANY_STATUS = "company_status";
    public static final String COMPANY_TYPE = "company_type";
    public static final String COMPANY_SUBTYPE = "company_subtype";
    public static final String DISSOLVED_SEARCH_ALPHABETICAL = "dissolved - alphabetical";
    public static final String INDEX = "index_name";
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
    public static final String OFFICER_NAME = "officer_name";
    public static final String UPSERT_OFFICER = "upsert_officer_id";
    
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

    public static Map<String, Object> getAdvancedSearchLogMap(
        AdvancedSearchQueryParams queryParams,
        String requestId,
        ConfiguredIndexNamesProvider indices) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date incorporatedFromDate = null;
        Date incorporatedToDate = null;
        Date dissolvedFromDate = null;
        Date dissolvedToDate = null;

        if(queryParams.getIncorporatedFrom() != null){
            incorporatedFromDate = Date.from(queryParams.getIncorporatedFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        if(queryParams.getIncorporatedTo() != null) {
            incorporatedToDate = Date.from(queryParams.getIncorporatedTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        if(queryParams.getDissolvedFrom() != null){
            dissolvedFromDate = Date.from(queryParams.getDissolvedFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        if(queryParams.getDissolvedTo() != null){
            dissolvedToDate = Date.from(queryParams.getDissolvedTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(requestId)
                .startIndex(String.valueOf(queryParams.getStartIndex()))
                .companyName(queryParams.getCompanyNameIncludes())
                .location(queryParams.getLocation())
                .incorporatedFrom(incorporatedFromDate)
                .incorporatedTo(incorporatedToDate)
                .companyStatus(queryParams.getCompanyStatusList())
                .sicCodes(queryParams.getSicCodes())
                .companyType(queryParams.getCompanyTypeList())
                .companySubtype(queryParams.getCompanySubtypeList())
                .dissolvedFrom(dissolvedFromDate)
                .dissolvedTo(dissolvedToDate)
                .companyNameExcludes(queryParams.getCompanyNameExcludes())
                .indexName(indices.advanced())
                .build().getLogMap();
        getLogger().info("advanced search filters", logMap);

        return logMap;
    }

    public static Map<String, Object> setUpDisqualificationUpsertLogging(
        Item disqualification,
        ConfiguredIndexNamesProvider indices) {
        String officerName;
        if (disqualification.getCorporateName() != null && disqualification.getCorporateName().length() > 0) {
            officerName = disqualification.getCorporateName();
        } else {
            officerName = disqualification.getForename() + " " + disqualification.getSurname();
        }
        return new DataMap.Builder()
                .officerName(officerName)
                .indexName(indices.primary())
                .build().getLogMap();
    }

    public static Map<String, Object> setUpOfficersAppointmentsUpsertLogging(
        String officerId, ConfiguredIndexNamesProvider indices) {
        return new DataMap.Builder()
                .officerId(officerId)
                .indexName(indices.primary())
                .build().getLogMap();
    }

    public static Map<String, Object> setUpPrimarySearchDeleteLogging(
        String officerId, ConfiguredIndexNamesProvider indices) {
        return new DataMap.Builder()
                .officerId(officerId)
                .indexName(indices.primary()).build().getLogMap();
    }

    public static Map<String, Object> setUpPrimaryOfficerSearchLogging(
            String officerId, String requestId, ConfiguredIndexNamesProvider indices) {
        return new DataMap.Builder()
                .officerId(officerId)
                .requestId(requestId)
                .indexName(indices.primary()).build().getLogMap();
    }


    public static Map<String, Object> setUpAlphabeticalSearchDeleteLogging(
            String companyName,
            ConfiguredIndexNamesProvider indices) {
        return new DataMap.Builder()
                .companyName(companyName)
                .indexName(indices.alphabetical()).build().getLogMap();
    }

    public static Map<String, Object> setUpCompanySearchCompanyUpsertLogging(
            String companyNumber,
            ConfiguredIndexNamesProvider indices) {
        return new DataMap.Builder()
                .companyNumber(companyNumber)
                .indexName(indices.primary())
                .build().getLogMap();
    }

    public static Map<String, Object> setUpCompanySearchCompanyDeleteLogging(
            String companyNumber,
            ConfiguredIndexNamesProvider indices) {
        return new DataMap.Builder()
                .companyNumber(companyNumber)
                .indexName(indices.primary()).build().getLogMap();
    }
}
