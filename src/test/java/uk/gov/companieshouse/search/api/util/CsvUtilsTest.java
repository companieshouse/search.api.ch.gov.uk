package uk.gov.companieshouse.search.api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvUtilsTest {

    @Test
    void testEscapeReservedCsvCharacters() {
        String regularString = "This is a test string";
        String stringWithComma = "This is a test, string";
        String stringWithQuote = "This is a \"test\" string";
        String stringWithCommaAndQuote = "This is a \"test\", string";
        String stringWithLineBreaks = "This is a \ntest \rstring";
        String stringWithAllReserved = "This is \na \"test\", \rstring";
        String arrayOfIds = "[123, 456, 789]";

        assertEquals("This is a test string", CsvUtils.escapeReservedCsvCharacters(regularString));
        assertEquals("\"This is a test, string\"" , CsvUtils.escapeReservedCsvCharacters(stringWithComma));
        assertEquals("This is a \"test\" string", CsvUtils.escapeReservedCsvCharacters(stringWithQuote));
        assertEquals("\"This is a \"\"test\"\", string\"", CsvUtils.escapeReservedCsvCharacters(stringWithCommaAndQuote));
        assertEquals("This is a test string", CsvUtils.escapeReservedCsvCharacters(stringWithLineBreaks));
        assertEquals("\"This is a \"\"test\"\", string\"", CsvUtils.escapeReservedCsvCharacters(stringWithAllReserved));
        assertEquals("\"[123, 456, 789]\"", CsvUtils.escapeReservedCsvCharacters(arrayOfIds));
    }
}