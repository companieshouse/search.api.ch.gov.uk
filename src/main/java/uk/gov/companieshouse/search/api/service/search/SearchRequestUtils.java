package uk.gov.companieshouse.search.api.service.search;

import org.elasticsearch.search.SearchHit;
import uk.gov.companieshouse.search.api.exception.SizeException;

import java.util.Map;

public class SearchRequestUtils {

    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";

    public static String getOrderedAlphaKeyWithId(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return (String) sourceAsMap.get(ORDERED_ALPHA_KEY_WITH_ID);
    }

    public static Integer checkResultsSize(Integer size, Integer maxSize) throws SizeException {

        if(size == null || size <= 0 || size > maxSize) {
            throw new SizeException("Size parameter is less than or equal to 0 or greater than maximum");
        }
        return size;
    }
}
