package uk.gov.companieshouse.search.api.interceptor;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.REQUEST_ID_HEADER_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.util.EricHeaderHelper;

@Component
public class UserAuthorisationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String identityType = EricHeaderHelper.getIdentityType(request);
        boolean isApiKeyRequest = identityType.equals(EricHeaderHelper.API_KEY_IDENTITY_TYPE);
        if(isApiKeyRequest) {
            return validateAPI(request, response);
        }
        Map<String, Object> logMap = new DataMap.Builder()
                .requestId(request.getHeader(REQUEST_ID_HEADER_NAME))
                .build().getLogMap();
        getLogger().error("Unrecognised identity type", logMap);
        response.setStatus(UNAUTHORIZED.value());
        return false;
    }

    private boolean validateAPI(HttpServletRequest request, HttpServletResponse response){
        DataMap.Builder builder = new DataMap.Builder()
                .requestId(request.getHeader(REQUEST_ID_HEADER_NAME));

        if(AuthorisationUtil.hasInternalUserRole(request) && PUT.matches(request.getMethod())) {
            getLogger().info("internal API is permitted to update the resource", builder.build().getLogMap());
            return true;
        } else {
            builder.status(UNAUTHORIZED.getReasonPhrase());
            getLogger().error("API is not permitted to perform a "+request.getMethod(), builder.build().getLogMap());
            response.setStatus(UNAUTHORIZED.value());
            return false;
        }
    }
}
