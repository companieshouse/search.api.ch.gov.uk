package uk.gov.companieshouse.search.api.constants;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class TestConstants {

    public static final String ACTIVE_COMPANY_STATUS = "active";
    public static final String ADVANCED_SEARCH_DEFAULT_SIZE = "ADVANCED_SEARCH_DEFAULT_SIZE";
    public static final String ADVANCED_SEARCH_MAX_SIZE = "ADVANCED_SEARCH_MAX_SIZE";
    public static final String BAD_COMPANY_STATUS = "aaa";
    public static final String BAD_DATE_FORMAT = "20010101";
    public static final String CIC_COMPANY_SUBTYPE = "community-interest-company";
    public static final String COMPANY_NAME = "test company";
    public static final String COMPANY_NAME_EXCLUDES = "test name excludes";
    public static final String COMPANY_NAME_INCLUDES = "test company";
    public static final String COMPANY_NUMBER = "00000000";
    public static final String DISSOLVED_FROM = "2017-01-01";
    public static final String DISSOLVED_TO = "2018-02-02";
    public static final String INCORPORATED_FROM = "2000-01-01";
    public static final String INCORPORATED_TO = "2002-02-02";
    public static final String LOCATION = "location";
    public static final String LTD_COMPANY_TYPE = "ltd";
    public static final String PFLP_COMPANY_SUBTYPE = "private-fund-limited-partnership";
    public static final String PLC_COMPANY_TYPE = "plc";
    public static final String REQUEST_ID = "requestID";
    public static final String SIC_CODES = "99960";

    public static final LocalDate DISSOLVED_FROM_MAPPED = LocalDate.of(2017, 1, 1);
    public static final LocalDate DISSOLVED_TO_MAPPED = LocalDate.of(2018, 2, 2);
    public static final LocalDate INCORPORATED_FROM_MAPPED = LocalDate.of(2000, 1, 1);
    public static final LocalDate INCORPORATED_TO_MAPPED = LocalDate.of(2002, 2, 2);

    public static final List<String> BAD_COMPANY_STATUS_LIST = Arrays.asList(BAD_COMPANY_STATUS);
    public static final List<String> COMPANY_STATUS_LIST = Arrays.asList(ACTIVE_COMPANY_STATUS);
    public static final List<String> SIC_CODES_LIST = Arrays.asList(SIC_CODES);
    public static final List<String> COMPANY_TYPES_LIST = Arrays.asList(LTD_COMPANY_TYPE, PLC_COMPANY_TYPE);
    public static final List<String> COMPANY_SUBTYPES_LIST = Arrays.asList(CIC_COMPANY_SUBTYPE, PFLP_COMPANY_SUBTYPE);

    public static final Integer SIZE = 20;
    public static final Integer START_INDEX = 0;

    public static final String ALPHABETICAL_RESPONSE =
            "{" +
                "\"ID\": \"id\"," +
                "\"company_type\": \"ltd\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"items\" : {" +
                    "\"company_number\" : \"00000000\"," +
                    "\"company_status\" : \"active\"," +
                    "\"corporate_name\" : \"TEST COMPANY\"" +
                "}," +
                "\"links\" : {" +
                    "\"self\" : \"/company/00000000\"" +
                 "}" +
            "}";

    public static final String DISSOLVED_RESPONSE =
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\"," +
                "\"registered_office_address\" : {" +
                    "\"address_line_1\" : \"addressLine1\"," +
                    "\"address_line_2\" : \"addressLine2\"," +
                    "\"locality\" : \"locality\"," +
                    "\"post_code\" : \"AB00 0 AB\"" +
                "}," +
                "\"date_of_cessation\" : \"20100501\"," +
                "\"date_of_creation\" : \"19890501\"," +
                "\"previous_company_names\" : [" +
                    "{" +
                        "\"name\" : \"TEST COMPANY 2\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"effective_from\" : \"19890101\"," +
                        "\"ceased_on\" : \"19920510\"," +
                        "\"company_number\" : \"00000000\"" +
                    "}" +
                "]" +
            "}";

    public static final String DISSOLVED_RESPONSE_POST_20_YEARS =
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\"," +
                "\"registered_office_address\" : {" +
                    "\"address_line_1\" : \"addressLine1\"," +
                    "\"address_line_2\" : \"addressLine2\"," +
                    "\"locality\" : \"locality\"," +
                    "\"post_code\" : \"AB00 0 AB\"" +
                "}," +
                "\"date_of_cessation\" : \"19910501\"," +
                "\"date_of_creation\" : \"19890501\"," +
                "\"previous_company_names\" : [" +
                    "{" +
                        "\"name\" : \"TEST COMPANY 2\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"effective_from\" : \"19890101\"," +
                        "\"ceased_on\" : \"19920510\"," +
                        "\"company_number\" : \"00000000\"" +
                    "}" +
                "]" +
            "}";

    public static final String DISSOLVED_RESPONSE_NO_DATES =
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\"," +
                "\"registered_office_address\" : {" +
                    "\"address_line_1\" : \"addressLine1\"," +
                    "\"address_line_2\" : \"addressLine2\"," +
                    "\"locality\" : \"locality\"," +
                    "\"post_code\" : \"AB00 0 AB\"" +
                "}," +
                "\"previous_company_names\" : [" +
                    "{" +
                        "\"name\" : \"TEST COMPANY 2\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"effective_from\" : \"19890101\"," +
                        "\"ceased_on\" : \"19920510\"," +
                        "\"company_number\" : \"00000000\"" +
                    "}" +
                "]" +
            "}";

    public static final String DISSOLVED_RESPONSE_NO_PREVIOUS_NAME =
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\"," +
                "\"registered_office_address\" : {" +
                    "\"address_line_1\" : \"addressLine1\"," +
                    "\"address_line_2\" : \"addressLine2\"," +
                    "\"locality\" : \"locality\"," +
                    "\"post_code\" : \"AB00 0 AB\"" +
                "}," +
                "\"date_of_cessation\" : \"20100501\"," +
                "\"date_of_creation\" : \"19890501\"" +
            "}";

    public static final String DISSOLVED_RESPONSE_NO_ADDRESS_LINES =
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\"," +
                "\"registered_office_address\" : {" +
                    "\"locality\" : \"locality\"," +
                    "\"post_code\" : \"AB00 0 AB\"" +
                "}," +
                "\"date_of_cessation\" : \"20100501\"," +
                "\"date_of_creation\" : \"19890501\"," +
                "\"previous_company_names\" : [" +
                    "{" +
                        "\"name\" : \"TEST COMPANY 2\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"effective_from\" : \"19890101\"," +
                        "\"ceased_on\" : \"19920510\"," +
                        "\"company_number\" : \"00000000\"" +
                    "}" +
                "]" +
            "}";

    public static final String DISSOLVED_RESPONSE_NO_LOCALITY =
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\"," +
                "\"registered_office_address\" : {" +
                    "\"address_line_1\" : \"addressLine1\"," +
                    "\"address_line_2\" : \"addressLine2\"," +
                    "\"post_code\" : \"AB00 0 AB\"" +
                "}," +
                "\"date_of_cessation\" : \"20100501\"," +
                "\"date_of_creation\" : \"19890501\"," +
                "\"previous_company_names\" : [" +
                    "{" +
                        "\"name\" : \"TEST COMPANY 2\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"effective_from\" : \"19890101\"," +
                        "\"ceased_on\" : \"19920510\"," +
                        "\"company_number\" : \"00000000\"" +
                    "}" +
                "]" +
            "}";

    public static final String DISSOLVED_RESPONSE_NO_POSTCODE =
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\"," +
                "\"registered_office_address\" : {" +
                    "\"address_line_1\" : \"addressLine1\"," +
                    "\"address_line_2\" : \"addressLine2\"," +
                    "\"locality\" : \"locality\"" +
                "}," +
                "\"date_of_cessation\" : \"20100501\"," +
                "\"date_of_creation\" : \"19890501\"," +
                "\"previous_company_names\" : [" +
                    "{" +
                        "\"name\" : \"TEST COMPANY 2\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"effective_from\" : \"19890101\"," +
                        "\"ceased_on\" : \"19920510\"," +
                        "\"company_number\" : \"00000000\"" +
                    "}" +
                "]" +
            "}";

    public static final String DISSOLVED_RESPONSE_NO_ROA =
            "{" +
                "\"company_number\" : \"00000000\"," +
                "\"company_name\" : \"TEST COMPANY\"," +
                "\"alpha_key\": \"alpha_key\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                "\"company_status\" : \"dissolved\"," +
                "\"date_of_cessation\" : \"20100501\"," +
                "\"date_of_creation\" : \"19890501\"," +
                "\"previous_company_names\" : [" +
                    "{" +
                        "\"name\" : \"TEST COMPANY 2\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"effective_from\" : \"19890101\"," +
                        "\"ceased_on\" : \"19920510\"," +
                        "\"company_number\" : \"00000000\"" +
                    "}" +
                "]" +
            "}";

    public static final String DISSOLVED_INNER_HITS =
            "{" +
                "\"ordered_alpha_key\" : \"PREVIOUSNAME\"," +
                "\"effective_from\" : \"19980309\"," +
                "\"ceased_on\" : \"19990428\"," +
                "\"name\" : \"PREVIOUS NAME LIMITED\"," +
                "\"company_number\" : \"00000000\"" +
            "}";

    public static final String ADVANCED_RESPONSE =
            "{" +
                "\"current_company\" : {" +
                    "\"company_number\" : \"00000000\"," +
                    "\"corporate_name\" : \"TEST COMPANY\"," +
                    "\"company_status\" : \"active\"," +
                    "\"sic_codes\" : [" +
                        "\"99960\"" +
                    "]," +
                    "\"address\" : {" +
                        "\"address_line_1\" : \"addressLine1\"," +
                        "\"address_line_2\" : \"addressLine2\"," +
                        "\"locality\" : \"locality\"," +
                        "\"post_code\" : \"AB00 0 AB\"," +
                        "\"country\" : \"wales\"," +
                        "\"premises\" : \"unit 1\"," +
                        "\"region\" : \"south\"" +
                    "}," +
                    "\"date_of_creation\" : \"1989-05-01\"" +
                "}," +
                "\"links\" : {" +
                    "\"self\" : \"/company/00000000\"" +
                "}," +
                "\"company_type\" : \"ltd\"" +
            "}";

    public static final String ADVANCED_RESPONSE_WITH_DISSOLVED_DATE =
    "{" +
        "\"current_company\" : {" +
            "\"company_number\" : \"00000000\"," +
            "\"corporate_name\" : \"TEST COMPANY\"," +
            "\"company_status\" : \"active\"," +
            "\"sic_codes\" : [" +
                "\"99960\"" +
            "]," +
            "\"address\" : {" +
                "\"address_line_1\" : \"addressLine1\"," +
                "\"address_line_2\" : \"addressLine2\"," +
                "\"locality\" : \"locality\"," +
                "\"post_code\" : \"AB00 0 AB\"," +
                "\"country\" : \"wales\"," +
                "\"premises\" : \"unit 1\"," +
                "\"region\" : \"south\"" +
            "}," +
            "\"date_of_cessation\" : \"2010-05-01\"," +
            "\"date_of_creation\" : \"1989-05-01\"" +
        "}," +
        "\"links\" : {" +
            "\"self\" : \"/company/00000000\"" +
        "}," +
        "\"company_type\" : \"ltd\"" +
    "}";

    public static final String ADVANCED_RESPONSE_DISSOLVED_COMPANY =
            "{" +
                "\"current_company\" : {" +
                    "\"company_number\" : \"00000000\"," +
                    "\"corporate_name\" : \"TEST COMPANY\"," +
                    "\"company_status\" : \"dissolved\"," +
                    "\"sic_codes\" : [" +
                        "\"99960\"" +
                    "]," +
                    "\"address\" : {" +
                        "\"address_line_1\" : \"addressLine1\"," +
                        "\"address_line_2\" : \"addressLine2\"," +
                        "\"locality\" : \"locality\"," +
                        "\"post_code\" : \"AB00 0 AB\"," +
                        "\"country\" : \"wales\"," +
                        "\"premises\" : \"unit 1\"," +
                        "\"region\" : \"south\"" +
                    "}," +
                    "\"date_of_cessation\" : \"2010-05-01\"," +
                    "\"date_of_creation\" : \"1989-05-01\"" +
                "}," +
                "\"links\" : {" +
                    "\"self\" : \"/company/00000000\"" +
                "}," +
                "\"company_type\" : \"ltd\"" +
            "}";

    public static final String ADVANCED_RESPONSE_MISSING_FIELDS =
            "{" +
                "\"current_company\" : {" +
                    "\"company_number\" : \"00000000\"," +
                    "\"corporate_name\" : \"TEST COMPANY\"," +
                    "\"company_status\" : \"dissolved\"," +
                    "\"date_of_cessation\" : null," +
                    "\"date_of_creation\" : null" +
                "}," +
                "\"links\" : {" +
                    "\"self\" : \"/company/00000000\"" +
                "}," +
                "\"company_type\" : \"ltd\"" +
            "}";
}
