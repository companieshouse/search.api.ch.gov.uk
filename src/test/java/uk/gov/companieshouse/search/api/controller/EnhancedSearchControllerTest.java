package uk.gov.companieshouse.search.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.enhanced.EnhancedSearchIndexService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnhancedSearchControllerTest {

    @Mock
    EnhancedSearchIndexService mockSearchIndexService;

    @Mock
    ApiToResponseMapper mockApiToResponseMapper;

    @InjectMocks
    private EnhancedSearchController enhancedSearchController;

    private static final String COMPANY_NAME = "test company";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String REQUEST_ID = "requestID";

    @Test
    @DisplayName("Test search found")
    void testSearchFound() {

        ResponseObject responseObject =
                new ResponseObject(SEARCH_FOUND, createSearchResults());

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(COMPANY_NAME);

        when(mockSearchIndexService.searchEnhanced(any(), anyString())).thenReturn(responseObject);
        when(mockApiToResponseMapper.map(responseObject))
                .thenReturn(ResponseEntity.status(FOUND).body(responseObject.getData()));

        ResponseEntity<?> responseEntity =
                enhancedSearchController.search(COMPANY_NAME, REQUEST_ID);

        assertNotNull(responseEntity);
        assertEquals(FOUND, responseEntity.getStatusCode());
    }

    private SearchResults<?> createSearchResults() {
        SearchResults<Company> searchResults = new SearchResults<>();
        List<Company> companies = new ArrayList<>();
        Company company = new Company();

        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyStatus(COMPANY_NUMBER);
        companies.add(company);

        TopHit topHit = new TopHit();
        topHit.setCompanyNumber("00004444");
        topHit.setCompanyName(COMPANY_NAME);

        searchResults.setItems(companies);
        searchResults.setEtag("test etag");
        searchResults.setTopHit(topHit);

        return searchResults;
    }
}