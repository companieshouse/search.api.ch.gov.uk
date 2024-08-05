package uk.gov.companieshouse.search.api.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.search.api.util.EricHeaderHelper.ERIC_IDENTITY;
import static uk.gov.companieshouse.search.api.util.EricHeaderHelper.ERIC_IDENTITY_TYPE;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EricHeaderHelperTest {

    @Mock
    private HttpServletRequest request;

    public static final String ERIC_IDENTITY_VALUE = "acbcdefghijklmnopqrstuvwxyz";
    public static final String ERIC_IDENTITY_TYPE_API_KEY_VALUE = "key";

    @Test
    void testGetIdentity() {
        when(request.getHeader(ERIC_IDENTITY)).thenReturn(ERIC_IDENTITY_VALUE);
        assertEquals(ERIC_IDENTITY_VALUE, EricHeaderHelper.getIdentity(request));
    }

    @Test
    void testGetIdentityType() {
        when(request.getHeader(ERIC_IDENTITY_TYPE)).thenReturn(ERIC_IDENTITY_TYPE_API_KEY_VALUE);
        assertEquals(ERIC_IDENTITY_TYPE_API_KEY_VALUE, EricHeaderHelper.getIdentityType(request));
    }

    @Test
    void testNullOnGetHeader() {
        when(request.getHeader(ERIC_IDENTITY_TYPE)).thenReturn(null);
        assertNull(null, EricHeaderHelper.getIdentityType(request));
    }

}
