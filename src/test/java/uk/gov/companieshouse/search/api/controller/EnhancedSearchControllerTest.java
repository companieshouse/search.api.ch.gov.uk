package uk.gov.companieshouse.search.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.mapper.EnhancedQueryParamMapper;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.enhanced.EnhancedSearchIndexService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnhancedSearchControllerTest {

    @Mock
    private EnhancedQueryParamMapper mockQueryParamMapper;

    @Mock
    private EnhancedSearchIndexService mockSearchIndexService;

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @InjectMocks
    private EnhancedSearchController enhancedSearchController;

    private static final String COMPANY_NAME = "test company";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String LOCATION = "location";
    private static final String INCORPORATED_FROM = "2000-1-1";
    private static final String INCORPORATED_TO = "2002-2-2";
    private static final String ACTIVE_COMPANY_STATUS = "active";
    private static final String SIC_CODES = "99960";
    private static final String LTD_COMPANY_TYPE = "ltd";
    private static final String PLC_COMPANY_TYPE = "plc";
    private static final String COMPANY_NAME_EXCLUDES = "test name excludes";
    private static final String REQUEST_ID = "requestID";

    private static final List<String> COMPANY_STATUS_LIST = Arrays.asList(ACTIVE_COMPANY_STATUS);
    private static final List<String> SIC_CODES_LIST = Arrays.asList(SIC_CODES);
    private static final List<String> COMPANY_TYPES_LIST = Arrays.asList(LTD_COMPANY_TYPE, PLC_COMPANY_TYPE);

    @Test
    @DisplayName("Test search found")
    void testSearchFound() throws Exception {

        ResponseObject responseObject =
                new ResponseObject(SEARCH_FOUND, createSearchResults());

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(COMPANY_NAME);
        enhancedSearchQueryParams.setSicCodes(SIC_CODES_LIST);

        when(mockQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, INCORPORATED_FROM,
            INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_NAME_EXCLUDES))
                .thenReturn(enhancedSearchQueryParams);
        when(mockSearchIndexService.searchEnhanced(any(), anyString())).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));

        ResponseEntity<?> responseEntity =
                enhancedSearchController.search(COMPANY_NAME, LOCATION, INCORPORATED_FROM, INCORPORATED_TO,
                    COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_NAME_EXCLUDES, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test date format exception caught")
    void testDateFormatException() throws Exception {

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(COMPANY_NAME);

        when(mockQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, INCORPORATED_FROM,
            INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_NAME_EXCLUDES))
                .thenThrow(DateFormatException.class);
        when(mockApiToResponseMapper.map(any()))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).body("Date format exception"));

        ResponseEntity<?> responseEntity =
                enhancedSearchController.search(COMPANY_NAME, LOCATION, INCORPORATED_FROM, INCORPORATED_TO,
                    COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_NAME_EXCLUDES, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test mapping exception caught")
    void testMappingException() throws Exception {

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(COMPANY_NAME);

        when(mockQueryParamMapper.mapEnhancedQueryParameters(COMPANY_NAME, LOCATION, INCORPORATED_FROM,
            INCORPORATED_TO, COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_NAME_EXCLUDES))
            .thenThrow(MappingException.class);
        when(mockApiToResponseMapper.map(any()))
            .thenReturn(ResponseEntity.status(BAD_REQUEST).body("Mapping exception"));

        ResponseEntity<?> responseEntity =
            enhancedSearchController.search(COMPANY_NAME, LOCATION, INCORPORATED_FROM, INCORPORATED_TO,
                COMPANY_STATUS_LIST, SIC_CODES_LIST, COMPANY_TYPES_LIST, COMPANY_NAME_EXCLUDES, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    private SearchResults<?> createSearchResults() {
        SearchResults<Company> searchResults = new SearchResults<>();
        List<Company> companies = new ArrayList<>();
        Company company = new Company();

        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyStatus(COMPANY_NUMBER);
        company.setSicCodes(SIC_CODES_LIST);
        companies.add(company);

        TopHit topHit = new TopHit();
        topHit.setCompanyNumber("00004444");
        topHit.setCompanyName(COMPANY_NAME);
        topHit.setSicCodes(SIC_CODES_LIST);

        searchResults.setItems(companies);
        searchResults.setEtag("test etag");
        searchResults.setTopHit(topHit);

        return searchResults;
    }
}