package uk.gov.companieshouse.search.api.util;

import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import uk.gov.companieshouse.search.api.model.esdatamodel.CorporateNameEndingsEnum;

public class CorporateOfficerNameUtils {

    private CorporateOfficerNameUtils() {
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
}
