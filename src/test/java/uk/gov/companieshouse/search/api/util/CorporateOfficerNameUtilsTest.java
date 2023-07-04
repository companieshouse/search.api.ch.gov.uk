package uk.gov.companieshouse.search.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.companieshouse.search.api.util.CorporateOfficerNameUtils.getCorporateNameEndings;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class CorporateOfficerNameUtilsTest {

    @Test
    void shouldGetCorporateNameEndings() {
        // given

        // when
        Pair<String, String> nameEndings = getCorporateNameEndings("corporate name ltd");

        // then
        assertEquals("corporate name", nameEndings.getLeft());
        assertEquals("ltd", nameEndings.getRight());
    }

    @Test
    void shouldGetCorporateNameEndingsNoEnding() {
        // given

        // when
        Pair<String, String> nameEndings = getCorporateNameEndings("corporate name without an ending");

        // then
        assertEquals("corporate name without an ending", nameEndings.getLeft());
        assertEquals("", nameEndings.getRight());
    }
}