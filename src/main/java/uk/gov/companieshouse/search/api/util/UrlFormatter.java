package uk.gov.companieshouse.search.api.util;

import java.nio.charset.StandardCharsets;
import org.springframework.web.util.UriUtils;

public final class UrlFormatter {

    private UrlFormatter() {
    }

    public static String urlEscape(final String input) {
        // Encodes using RFC 3986 - same as the Perl did (different to URLEncoder.encode())
        // Required to work with the Alpha Key service
        return UriUtils.encodePath(input, StandardCharsets.UTF_8);
    }
}
