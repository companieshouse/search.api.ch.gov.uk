package uk.gov.companieshouse.search.api.interceptor;

import io.netty.handler.codec.http.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.util.security.EricConstants;
import uk.gov.companieshouse.api.util.security.SecurityConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.search.util.EricHeaderHelper.ERIC_IDENTITY_TYPE;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAuthorisationInterceptorTest {

    @InjectMocks
    private UserAuthorisationInterceptor userAuthorisationInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private static final String ERIC_IDENTITY_TYPE_API_KEY_VALUE = "key";
    private static final String INVALID_IDENTITY_TYPE_VALUE = "test";

    @Test
    @DisplayName("Does not Authorise an external API key is used")
    void willNotAuthoriseIfRequestIsPutAndExternalAPIKey() {
        when(request.getHeader(ERIC_IDENTITY_TYPE)).thenReturn(ERIC_IDENTITY_TYPE_API_KEY_VALUE);
        assertFalse(userAuthorisationInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Authorise if PUT and an internal API key is used")
    void willAuthoriseIfRequestIsPutAndInternalAPIKey() {
        when(request.getMethod()).thenReturn(HttpMethod.PUT.toString());
        doReturn("request-id").when(request).getHeader("X-Request-ID");
        doReturn(ERIC_IDENTITY_TYPE_API_KEY_VALUE).when(request).getHeader(ERIC_IDENTITY_TYPE);
        doReturn(SecurityConstants.INTERNAL_USER_ROLE).when(request).getHeader(EricConstants.ERIC_AUTHORISED_KEY_ROLES);
        assertTrue(userAuthorisationInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Does not Authorise if PUT and unrecognised identity type")
    public void willNotAuthoriseIfRequestIsPostAndUnrecognisedIdentity() {
        when(request.getHeader(ERIC_IDENTITY_TYPE)).thenReturn(INVALID_IDENTITY_TYPE_VALUE);
        assertFalse(userAuthorisationInterceptor.preHandle(request, response, null));
    }
}
