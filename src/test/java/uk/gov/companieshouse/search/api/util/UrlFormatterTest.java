package uk.gov.companieshouse.search.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UrlFormatterTest {

    @ParameterizedTest
    @CsvSource({
            "Company {abc} name , Company+%7Babc%7D+name",
            "Company {abc name , Company+%7Babc+name",
            "Company { abc } name , Company+%7B+abc+%7D+name",
            "Company {abc } name , Company+%7Babc+%7D+name",
            "Company { abc} name , Company+%7B+abc%7D+name",
            "Company abc} name , Company+abc%7D+name",
            "Company abc name , Company+abc+name",
    })
    void removeCurlyBraces(final String input, final String expected) {
        assertEquals(expected, UrlFormatter.urlEscape(input));
    }
}