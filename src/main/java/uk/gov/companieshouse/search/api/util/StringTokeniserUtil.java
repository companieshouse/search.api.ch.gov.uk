package uk.gov.companieshouse.search.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringTokeniserUtil {

    public static List<String> tokeniseString(String str) {
        List<String> tokens = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {

            if (i != str.length() - 1) {
                String resultString = str.substring(0, str.length() - i);
                tokens.add(resultString);
            }
        }
        Collections.reverse(tokens);

        return tokens;
    }
}
