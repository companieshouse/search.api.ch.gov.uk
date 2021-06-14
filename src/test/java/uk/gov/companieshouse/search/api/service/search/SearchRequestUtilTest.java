package uk.gov.companieshouse.search.api.service.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.search.api.exception.SizeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchRequestUtilTest {

    @Test
    @DisplayName("Checks that the size exception is thrown if the size param is 0")
    void checkSizeExceptionIsThrownSizeIsZero() throws SizeException {
        assertThrows(SizeException.class, () -> {
            SearchRequestUtils.checkResultsSize(0, 50);
        });
    }

    @Test
    @DisplayName("Checks that the size exception is thrown if the size param is negative")
    void checkSizeExceptionIsThrownSizeIsNegative() throws SizeException {
        assertThrows(SizeException.class, () -> {
            SearchRequestUtils.checkResultsSize(-5, 50);
        });
    }

    @Test
    @DisplayName("Checks that the size exception is thrown if the size param is null")
    void checkSizeExceptionIsThrownSizeIsNull() throws SizeException {
        assertThrows(SizeException.class, () -> {
            SearchRequestUtils.checkResultsSize(null, 50);
        });
    }

    @Test
    @DisplayName("Checks that the size exception is thrown if the size param is greater than maximum")
    void checkSizeExceptionIsThrownSizeIsGreaterThanMax() throws SizeException {
        assertThrows(SizeException.class, () -> {
            SearchRequestUtils.checkResultsSize(101, 50);
        });
    }

    @Test
    @DisplayName("Checks that the requested value is returned if the size param is valid")
    void checkValueIsReturnedIfSizeIsValid() throws SizeException {
        assertEquals(new Integer(30), SearchRequestUtils.checkResultsSize(30,  50));
    }
}
