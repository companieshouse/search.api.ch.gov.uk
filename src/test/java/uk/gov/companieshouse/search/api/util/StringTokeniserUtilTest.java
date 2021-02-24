package uk.gov.companieshouse.search.api.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringTokeniserUtilTest {

    @Test
    @DisplayName("Test tokenising of string successful")
    void tokeniseString() {
        String testString = "ABCDEFGH";
        List<String> tokens = StringTokeniserUtil.tokeniseString(testString);

        assertEquals(true, tokens.contains("AB"));
        assertEquals(true, tokens.contains("ABC"));
        assertEquals(true, tokens.contains("ABCD"));
        assertEquals(true, tokens.contains("ABCDE"));
        assertEquals(true, tokens.contains("ABCDEF"));
        assertEquals(true, tokens.contains("ABCDEFG"));
        assertEquals(true, tokens.contains("ABCDEFGH"));
    }
}
