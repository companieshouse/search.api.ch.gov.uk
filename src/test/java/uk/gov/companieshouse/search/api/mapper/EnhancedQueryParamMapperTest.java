package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnhancedQueryParamMapperTest {

    private static final String COMPANY_NAME = "test company";
    private static final String LOCATION = "location";
    private static final String INCORPORATED_FROM = "2000-01-01";
    private static final LocalDate INCORPORATED_FROM_MAPPED = LocalDate.of(2000, 1, 1);
    private static final String INCORPORATED_TO = "2002-02-02";
    private static final LocalDate INCORPORATED_TO_MAPPED = LocalDate.of(2002, 2, 2);
    private static final String BAD_DATE_FORMAT = "20010101";

    @Test
    @DisplayName("Test params mapped successfully")
    void testMapParamsSuccessful() throws Exception {

        EnhancedQueryParamMapper enhancedQueryParamMapper = new EnhancedQueryParamMapper();
        EnhancedSearchQueryParams enhancedSearchQueryParams =
                enhancedQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, INCORPORATED_FROM, INCORPORATED_TO);

        assertEquals(COMPANY_NAME, enhancedSearchQueryParams.getCompanyName());
        assertEquals(LOCATION, enhancedSearchQueryParams.getLocation());
        assertEquals(INCORPORATED_FROM_MAPPED, enhancedSearchQueryParams.getIncorporatedFrom());
        assertEquals(INCORPORATED_TO_MAPPED, enhancedSearchQueryParams.getIncorporatedTo());
    }

    @Test
    @DisplayName("Test params mapped successfully")
    void testDateFormatExceptionThrown() throws Exception {

        EnhancedQueryParamMapper enhancedQueryParamMapper = new EnhancedQueryParamMapper();

        assertThrows(DateFormatException.class, () -> {
                enhancedQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, BAD_DATE_FORMAT, BAD_DATE_FORMAT);
        });
    }
}
