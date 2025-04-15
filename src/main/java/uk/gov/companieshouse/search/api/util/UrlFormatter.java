package uk.gov.companieshouse.search.api.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class UrlFormatter {

    private UrlFormatter() {
    }

    public static String urlEscape(final String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }
}
