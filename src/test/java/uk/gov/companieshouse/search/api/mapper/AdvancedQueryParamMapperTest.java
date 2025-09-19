package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static uk.gov.companieshouse.search.api.constants.TestConstants.ACTIVE_COMPANY_STATUS;
import static uk.gov.companieshouse.search.api.constants.TestConstants.ADVANCED_SEARCH_DEFAULT_SIZE;
import static uk.gov.companieshouse.search.api.constants.TestConstants.ADVANCED_SEARCH_MAX_SIZE;
import static uk.gov.companieshouse.search.api.constants.TestConstants.BAD_COMPANY_STATUS_LIST;
import static uk.gov.companieshouse.search.api.constants.TestConstants.BAD_DATE_FORMAT;
import static uk.gov.companieshouse.search.api.constants.TestConstants.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.constants.TestConstants.COMPANY_NAME_EXCLUDES;
import static uk.gov.companieshouse.search.api.constants.TestConstants.COMPANY_STATUS_LIST;
import static uk.gov.companieshouse.search.api.constants.TestConstants.COMPANY_SUBTYPES_LIST;
import static uk.gov.companieshouse.search.api.constants.TestConstants.COMPANY_TYPES_LIST;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_FROM;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_FROM_MAPPED;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_TO;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_TO_MAPPED;
import static uk.gov.companieshouse.search.api.constants.TestConstants.INCORPORATED_FROM;
import static uk.gov.companieshouse.search.api.constants.TestConstants.INCORPORATED_FROM_MAPPED;
import static uk.gov.companieshouse.search.api.constants.TestConstants.INCORPORATED_TO;
import static uk.gov.companieshouse.search.api.constants.TestConstants.INCORPORATED_TO_MAPPED;
import static uk.gov.companieshouse.search.api.constants.TestConstants.LOCATION;
import static uk.gov.companieshouse.search.api.constants.TestConstants.SIC_CODES_LIST;
import static uk.gov.companieshouse.search.api.constants.TestConstants.SIZE;
import static uk.gov.companieshouse.search.api.constants.TestConstants.START_INDEX;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvancedQueryParamMapperTest {

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @InjectMocks
    private AdvancedQueryParamMapper advancedQueryParamMapper;

    @Test
    @DisplayName("Test params mapped successfully")
    void testMapParamsSuccessful() throws Exception {

        doReturn(500).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE);

        AdvancedSearchQueryParams advancedSearchQueryParams =
            advancedQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME, LOCATION,
                INCORPORATED_FROM, INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST,
                COMPANY_SUBTYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE);

        assertEquals(START_INDEX, advancedSearchQueryParams.getStartIndex());
        assertEquals(COMPANY_NAME, advancedSearchQueryParams.getCompanyNameIncludes());
        assertEquals(LOCATION, advancedSearchQueryParams.getLocation());
        assertEquals(COMPANY_TYPES_LIST, advancedSearchQueryParams.getCompanyTypeList());
        assertEquals(COMPANY_SUBTYPES_LIST, advancedSearchQueryParams.getCompanySubtypeList());
        assertEquals(INCORPORATED_FROM_MAPPED, advancedSearchQueryParams.getIncorporatedFrom());
        assertEquals(INCORPORATED_TO_MAPPED, advancedSearchQueryParams.getIncorporatedTo());
        assertEquals(DISSOLVED_FROM_MAPPED, advancedSearchQueryParams.getDissolvedFrom());
        assertEquals(DISSOLVED_TO_MAPPED, advancedSearchQueryParams.getDissolvedTo());
        assertEquals(ACTIVE_COMPANY_STATUS, advancedSearchQueryParams.getCompanyStatusList().get(0));
        assertEquals(SIC_CODES_LIST, advancedSearchQueryParams.getSicCodes());
        assertEquals(SIZE, advancedSearchQueryParams.getSize());
    }

    @Test
    @DisplayName("Test start index set to 0 when start index is null")
    void testMapParamsSuccessfulNoStartIndex() throws Exception {

        doReturn(500).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE);

        AdvancedSearchQueryParams advancedSearchQueryParams =
            advancedQueryParamMapper.mapAdvancedQueryParameters(null, COMPANY_NAME, LOCATION,
                INCORPORATED_FROM, INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST,
                COMPANY_SUBTYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE);

        assertEquals(START_INDEX, advancedSearchQueryParams.getStartIndex());
    }

    @Test
    @DisplayName("Test start index set to 0 when start index is less than 0")
    void testMapParamsSuccessfulStartIndexBelowZero() throws Exception {

        doReturn(500).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE);

        AdvancedSearchQueryParams advancedSearchQueryParams =
            advancedQueryParamMapper.mapAdvancedQueryParameters(-1, COMPANY_NAME, LOCATION, INCORPORATED_FROM,
                INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_SUBTYPES_LIST,
                DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE);

        assertEquals(START_INDEX, advancedSearchQueryParams.getStartIndex());
    }

    @Test
    @DisplayName("Test params mapped successfully no dates")
    void testMapParamsSuccessfulNoDates() throws Exception {

        doReturn(500).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE);

        AdvancedSearchQueryParams advancedSearchQueryParams =
            advancedQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME, LOCATION, null,
                null, null, null, null, null,
                null, null, null, null);

        assertEquals(COMPANY_NAME, advancedSearchQueryParams.getCompanyNameIncludes());
        assertEquals(LOCATION, advancedSearchQueryParams.getLocation());
        assertNull(advancedSearchQueryParams.getIncorporatedFrom());
        assertNull(advancedSearchQueryParams.getIncorporatedTo());
    }

    @Test
    @DisplayName("Test date format exception thrown")
    void testDateFormatExceptionThrown() {

        assertThrows(DateFormatException.class, () -> {
            advancedQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME, LOCATION, BAD_DATE_FORMAT,
                BAD_DATE_FORMAT, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_SUBTYPES_LIST,
                BAD_DATE_FORMAT, BAD_DATE_FORMAT, COMPANY_NAME_EXCLUDES, null);
        });
    }

    @Test
    @DisplayName("Test mapping exception thrown")
    void testMappingExceptionThrown() {

        assertThrows(MappingException.class, () -> {
            advancedQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME, LOCATION, INCORPORATED_FROM,
                INCORPORATED_TO, BAD_COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_SUBTYPES_LIST,
                DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, null);
        });
    }

    @Test
    @DisplayName("Test size exception thrown when size is greater than max allowed")
    void testSizeExceptionThrownSizeGreaterThanMax() {

        doReturn(500).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE);

        assertThrows(SizeException.class, () -> {
            advancedQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME, null, null, null,
                null, null, null,null, null,
                null, null, 50000);
        });
    }

    @Test
    @DisplayName("Test size exception thrown when size is 0")
    void testSizeExceptionThrownSizeIsZero() {

        doReturn(500).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE);

        assertThrows(SizeException.class, () -> {
            advancedQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME, null, null, null,
                    null, null, null,null, null, null, null, 0);
        });
    }

    @Test
    @DisplayName("Test size exception thrown when size is less than 0")
    void testSizeExceptionThrownSizeIsLessThanZero() {

        doReturn(500).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE);

        assertThrows(SizeException.class, () -> {
            advancedQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME, null, null, null,
                    null, null, null, null, null, null, null, -50);
        });
    }

    @Test
    @DisplayName("Test size is set to default if size is null")
    void testSizeSetToDefault() throws Exception {

        doReturn(500).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE);
        doReturn(20).when(mockEnvironmentReader).getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE);

        AdvancedSearchQueryParams advancedSearchQueryParams =
                advancedQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME, LOCATION, INCORPORATED_FROM,
                    INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_SUBTYPES_LIST,
                    DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, null);

        assertEquals(SIZE, advancedSearchQueryParams.getSize());
    }
}
