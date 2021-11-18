package uk.gov.companieshouse.search.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.mapper.AdvancedQueryParamMapper;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchIndexService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvancedSearchControllerTest {

    @Mock
    private AdvancedQueryParamMapper mockQueryParamMapper;

    @Mock
    private AdvancedSearchIndexService mockSearchIndexService;

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @InjectMocks
    private AdvancedSearchController advancedSearchController;

    private static final Integer START_INDEX = 0;
    private static final String COMPANY_NAME_INCLUDES = "test company";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String LOCATION = "location";
    private static final String INCORPORATED_FROM = "2000-1-1";
    private static final String INCORPORATED_TO = "2002-2-2";
    private static final String DISSOLVED_FROM = "2017-1-1";
    private static final String DISSOLVED_TO = "2018-2-2";
    private static final String ACTIVE_COMPANY_STATUS = "active";
    private static final String SIC_CODES = "99960";
    private static final String LTD_COMPANY_TYPE = "ltd";
    private static final String PLC_COMPANY_TYPE = "plc";
    private static final String COMPANY_NAME_EXCLUDES = "test name excludes";
    private static final String REQUEST_ID = "requestID";
    private static final Integer SIZE = 20;

    private static final List<String> COMPANY_STATUS_LIST = Arrays.asList(ACTIVE_COMPANY_STATUS);
    private static final List<String> SIC_CODES_LIST = Arrays.asList(SIC_CODES);
    private static final List<String> COMPANY_TYPES_LIST = Arrays.asList(LTD_COMPANY_TYPE, PLC_COMPANY_TYPE);

    @Test
    @DisplayName("Test search found")
    void testSearchFound() throws Exception {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_FOUND, createSearchResults());

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME_INCLUDES);
        advancedSearchQueryParams.setSicCodes(SIC_CODES_LIST);

        when(mockQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME_INCLUDES, LOCATION, INCORPORATED_FROM,
            INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE))
            .thenReturn(advancedSearchQueryParams);
        when(mockSearchIndexService.searchAdvanced(any(), anyString())).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
            .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));

        ResponseEntity<?> responseEntity =
            advancedSearchController.search(START_INDEX, COMPANY_NAME_INCLUDES, LOCATION, INCORPORATED_FROM, INCORPORATED_TO,
                COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE,
                REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test date format exception caught")
    void testDateFormatException() throws Exception {

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME_INCLUDES);

        when(mockQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME_INCLUDES, LOCATION, INCORPORATED_FROM,
            INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE))
            .thenThrow(DateFormatException.class);
        when(mockApiToResponseMapper.map(any()))
            .thenReturn(ResponseEntity.status(BAD_REQUEST).body("Date format exception"));

        ResponseEntity<?> responseEntity =
            advancedSearchController.search(START_INDEX, COMPANY_NAME_INCLUDES, LOCATION, INCORPORATED_FROM, INCORPORATED_TO,
                COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE,
                REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test mapping exception caught")
    void testMappingException() throws Exception {

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME_INCLUDES);

        when(mockQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME_INCLUDES, LOCATION, INCORPORATED_FROM,
            INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE))
            .thenThrow(MappingException.class);
        when(mockApiToResponseMapper.map(any()))
            .thenReturn(ResponseEntity.status(BAD_REQUEST).body("Mapping exception"));

        ResponseEntity<?> responseEntity =
            advancedSearchController.search(START_INDEX, COMPANY_NAME_INCLUDES, LOCATION, INCORPORATED_FROM, INCORPORATED_TO,
                COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE,  REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test size exception caught")
    void testSizeException() throws Exception {

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();
        advancedSearchQueryParams.setCompanyNameIncludes(COMPANY_NAME_INCLUDES);
        advancedSearchQueryParams.setSize(1000);

        when(mockQueryParamMapper.mapAdvancedQueryParameters(START_INDEX, COMPANY_NAME_INCLUDES, LOCATION, INCORPORATED_FROM,
                INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE))
                .thenThrow(SizeException.class);
        when(mockApiToResponseMapper.map(any()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).body("Size exception"));

        ResponseEntity<?> responseEntity =
                advancedSearchController.search(START_INDEX, COMPANY_NAME_INCLUDES, LOCATION, INCORPORATED_FROM, INCORPORATED_TO,
                        COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, DISSOLVED_FROM, DISSOLVED_TO, COMPANY_NAME_EXCLUDES, SIZE,
                        REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    private SearchResults<?> createSearchResults() {
        SearchResults<Company> searchResults = new SearchResults<>();
        List<Company> companies = new ArrayList<>();
        Company company = new Company();

        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyName(COMPANY_NAME_INCLUDES);
        company.setCompanyStatus(COMPANY_NUMBER);
        company.setSicCodes(SIC_CODES_LIST);
        companies.add(company);

        TopHit topHit = new TopHit();
        topHit.setCompanyNumber("00004444");
        topHit.setCompanyName(COMPANY_NAME_INCLUDES);
        topHit.setSicCodes(SIC_CODES_LIST);

        searchResults.setItems(companies);
        searchResults.setEtag("test etag");
        searchResults.setTopHit(topHit);

        return searchResults;
    }
}