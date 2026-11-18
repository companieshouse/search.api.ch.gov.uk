package uk.gov.companieshouse.search.api.util;

public class CsvUtils {

    public static String escapeReservedCsvCharacters(String str) {
        if (!str.contains(",")) {
            return str;
        }
        if (str.contains("\",")) {
            str= str.replace("\"", "\"\"");
        }
        return "\"" + str + "\"";
    }

}
