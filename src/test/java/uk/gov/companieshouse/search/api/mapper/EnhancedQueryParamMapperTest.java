package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
    private static final String ACTIVE_COMPANY_STATUS = "active";
    private static final List<String> COMPANY_STATUS_LIST = Arrays.asList(ACTIVE_COMPANY_STATUS);
    private static final String BAD_COMPANY_STATUS = "aaa";
    private static final List<String> BAD_COMPANY_STATUS_LIST = Arrays.asList(BAD_COMPANY_STATUS);
    private static final String SIC_CODES = "99960";
    private static final List<String> SIC_CODES_LIST = Arrays.asList(SIC_CODES);

    @Test
    @DisplayName("Test params mapped successfully")
    void testMapParamsSuccessful() throws Exception {

        EnhancedQueryParamMapper enhancedQueryParamMapper = new EnhancedQueryParamMapper();
        EnhancedSearchQueryParams enhancedSearchQueryParams =
            enhancedQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, INCORPORATED_FROM,
                INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST);

        assertEquals(COMPANY_NAME, enhancedSearchQueryParams.getCompanyName());
        assertEquals(LOCATION, enhancedSearchQueryParams.getLocation());
        assertEquals(INCORPORATED_FROM_MAPPED, enhancedSearchQueryParams.getIncorporatedFrom());
        assertEquals(INCORPORATED_TO_MAPPED, enhancedSearchQueryParams.getIncorporatedTo());
        assertEquals(ACTIVE_COMPANY_STATUS, enhancedSearchQueryParams.getCompanyStatusList().get(0));
        assertEquals(SIC_CODES_LIST, enhancedSearchQueryParams.getSicCodes());
    }

    @Test
    @DisplayName("Test params mapped successfully no dates")
    void testMapParamsSuccessfulNoDates() throws Exception {

        EnhancedQueryParamMapper enhancedQueryParamMapper = new EnhancedQueryParamMapper();
        EnhancedSearchQueryParams enhancedSearchQueryParams =
            enhancedQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, null,
                null, null, null);

        assertEquals(COMPANY_NAME, enhancedSearchQueryParams.getCompanyName());
        assertEquals(LOCATION, enhancedSearchQueryParams.getLocation());
        assertNull(enhancedSearchQueryParams.getIncorporatedFrom());
        assertNull(enhancedSearchQueryParams.getIncorporatedTo());
    }

    @Test
    @DisplayName("Test date format exception thrown")
    void testDateFormatExceptionThrown() {

        EnhancedQueryParamMapper enhancedQueryParamMapper = new EnhancedQueryParamMapper();

        assertThrows(DateFormatException.class, () -> {
            enhancedQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, BAD_DATE_FORMAT,
                BAD_DATE_FORMAT, COMPANY_STATUS_LIST, SIC_CODES_LIST);
        });
    }

    @Test
    @DisplayName("Test mapping exception thrown")
    void testMappingExceptionThrown() {

        EnhancedQueryParamMapper enhancedQueryParamMapper = new EnhancedQueryParamMapper();

        assertThrows(MappingException.class, () -> {
            enhancedQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, INCORPORATED_FROM,
                INCORPORATED_TO, BAD_COMPANY_STATUS_LIST, SIC_CODES_LIST);
        });
    }
}
