package uk.gov.companieshouse.search.api.util;

import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import uk.gov.companieshouse.search.api.model.esdatamodel.CorporateNameEndingsEnum;

public class OfficerNameUtils {

    private static final List<String> PERSON_TITLES = List.of("sir", "lord", "doctor", "dr");
    private OfficerNameUtils() {
    }

    public static Pair<String, String> getCorporateNameEndings(String name) {
        String corporateNameStart = name;
        String corporateNameEnd = "";
        for (CorporateNameEndingsEnum ending : CorporateNameEndingsEnum.values()) {
            Pattern pattern = Pattern.compile("\\s" + ending.getEnding() + "$", Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(name).find() && ending.getEnding().length() > 0) {
                corporateNameStart = name.substring(0, name.length() - ending.getEnding().length() - 1);
                corporateNameEnd = name.substring(name.length() - ending.getEnding().length());
            }
        }
        return Pair.of(corporateNameStart, corporateNameEnd);
    }

    public static String getPersonTitle(String title) {
        return PERSON_TITLES.contains(title.toLowerCase()) ? title : null;
    }
}
