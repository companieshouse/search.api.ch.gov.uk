package uk.gov.companieshouse.search.api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvUtilsTest {

    @Test
    void testEscapeReservedCsvCharacters() {
        String REGULAR_STRING = "This is a test string";
        String STRING_WITH_COMMA = "This is a test, string";
        String STRING_WITH_QUOTE = "This is a \"test\" string";
        String STRING_WITH_COMMA_AND_QUOTE = "This is a \"test\", string";
        String ARRAY_OF_IDS = "[123, 456, 789]";

        assertEquals("This is a test string", CsvUtils.escapeReservedCsvCharacters(REGULAR_STRING));
        assertEquals("\"This is a test, string\"" , CsvUtils.escapeReservedCsvCharacters(STRING_WITH_COMMA));
        assertEquals("This is a \"test\" string", CsvUtils.escapeReservedCsvCharacters(STRING_WITH_QUOTE));
        assertEquals("\"This is a \"\"test\"\", string\"", CsvUtils.escapeReservedCsvCharacters(STRING_WITH_COMMA_AND_QUOTE));
        assertEquals("\"[123, 456, 789]\"", CsvUtils.escapeReservedCsvCharacters(ARRAY_OF_IDS));
    }
}