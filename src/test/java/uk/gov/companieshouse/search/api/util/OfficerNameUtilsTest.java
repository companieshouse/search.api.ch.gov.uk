package uk.gov.companieshouse.search.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.search.api.util.OfficerNameUtils.getCorporateNameEndings;
import static uk.gov.companieshouse.search.api.util.OfficerNameUtils.getPersonTitle;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class OfficerNameUtilsTest {

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

    @Test
    void shouldGetPersonTitle() {
        // given

        // when
        String actual = getPersonTitle("Dr");

        // then
        assertEquals("Dr", actual);
    }

    @Test
    void shouldGetNullPersonTitle() {
        // given

        // when
        String actual = getPersonTitle("Mr");

        // then
        assertNull(actual);
    }

    @Test
    void shouldGetNullPersonTitleWhenNullProvided() {
        // given

        // when
        String actual = getPersonTitle(null);

        // then
        assertNull(actual);
    }
}