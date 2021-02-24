package uk.gov.companieshouse.search.api.util;

import com.sun.tools.corba.se.idl.toJavaPortable.Helper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    @DisplayName("Test instantiating private constructor throws exception")
    public void privateConstructorTest() throws Exception {
        Constructor<StringTokeniserUtil> constructor = StringTokeniserUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);

        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }
}
