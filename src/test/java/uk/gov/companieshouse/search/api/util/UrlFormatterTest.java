package uk.gov.companieshouse.search.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UrlFormatterTest {

    @ParameterizedTest
    @MethodSource("testArgs")
    void removeCurlyBraces(final String input, final String expected) {
        assertEquals(expected, UrlFormatter.urlEscape(input));
    }

    private static Stream<Arguments> testArgs() {
        return Stream.of(
                Arguments.of("Company {abc} name", "Company%20%7Babc%7D%20name"),
                Arguments.of("Company {abc name", "Company%20%7Babc%20name"),
                Arguments.of("Company abc} name", "Company%20abc%7D%20name")
        );
    }
}