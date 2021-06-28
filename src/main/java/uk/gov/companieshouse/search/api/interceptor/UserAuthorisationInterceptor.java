package uk.gov.companieshouse.search.api.interceptor;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.REQUEST_ID_HEADER_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.REQUEST_ID_LOG_KEY;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.STATUS_LOG_KEY;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;
import uk.gov.companieshouse.search.util.EricHeaderHelper;

@Component
public class UserAuthorisationInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String identityType = EricHeaderHelper.getIdentityType(request);
        boolean isApiKeyRequest = identityType.equals(EricHeaderHelper.API_KEY_IDENTITY_TYPE);
        if(isApiKeyRequest) {
            return validateAPI(request, response);
        }
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(REQUEST_ID_LOG_KEY, request.getHeader(REQUEST_ID_HEADER_NAME));
        getLogger().error("Unrecognised identity type", logMap);
        response.setStatus(UNAUTHORIZED.value());
        return false;
    }
    private boolean validateAPI(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(REQUEST_ID_LOG_KEY, request.getHeader(REQUEST_ID_HEADER_NAME));
        if(AuthorisationUtil.hasInternalUserRole(request) && GET.matches(request.getMethod())) {
            getLogger().info("internal API is permitted to view the resource", logMap);
            return true;
        } else {
            logMap.put(STATUS_LOG_KEY, UNAUTHORIZED);
            getLogger().error("API is not permitted to perform a "+request.getMethod(), logMap);
            response.setStatus(UNAUTHORIZED.value());
            return false;
        }
    }
}
