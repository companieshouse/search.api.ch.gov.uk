package uk.gov.companieshouse.search.api.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class EricHeaderHelper {
    public static final String API_KEY_IDENTITY_TYPE        = "key";
    public static final String ERIC_IDENTITY                = "ERIC-Identity";
    public static final String ERIC_IDENTITY_TYPE           = "ERIC-Identity-Type";

    private EricHeaderHelper() { }

    public static String getIdentity(HttpServletRequest request) {
        return getHeader(request, ERIC_IDENTITY);
    }

    public static String getIdentityType(HttpServletRequest request) {
        return getHeader(request, ERIC_IDENTITY_TYPE);
    }

    private static String getHeader(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        if (StringUtils.isNotBlank(headerValue)) {
            return headerValue;
        } else {
            return null;
        }
    }

}
