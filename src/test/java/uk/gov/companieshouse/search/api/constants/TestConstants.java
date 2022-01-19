package uk.gov.companieshouse.search.api.constants;

public class TestConstants {

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
                "\"date_of_cessation\" : \"2010-05-01\"" +
            "}," +
            "\"links\" : {" +
                "\"self\" : \"/company/00000000\"" +
            "}," +
            "\"company_type\" : \"ltd\"" +
        "}";
}
