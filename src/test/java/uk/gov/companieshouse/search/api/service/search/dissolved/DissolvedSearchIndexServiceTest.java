package uk.gov.companieshouse.search.api.service.search.dissolved;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.exception.SearchException;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchRequestService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DissolvedSearchIndexServiceTest {

    @InjectMocks
    private DissolvedSearchIndexService searchIndexService;

    @Mock
    private DissolvedSearchRequestService mockDissolvedSearchRequestService;

    private static final String REQUEST_ID = "requestId";
    private static final String COMPANY_NAME = "companyName";
    private static final String COMPANY_NUMBER = "companyName";
    private static final String COMPANY_STATUS = "companyStatus";
    private static final String DATE_OF_CESSATION = "01/01/1993";
    private static final String DATE_OF_CREATION = "01/01/1983";
    private static final String DATE_OF_NAME_CESSATION = "01/01/1963";
    private static final String DATE_OF_NAME_EFFECTIVENESS = "01/01/1973";
    private static final String LOCALITY = "locality";
    private static final String POSTAL_CODE = "AB12 C34";
    private static final String PREVIOUS_NAME = "previousName";


    @Test
    @DisplayName("Test search request returns successfully")
    void searchRequestSuccessful() throws Exception {
        when(mockDissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID))
                .thenReturn(createSearchResults(true));
        DissolvedResponseObject responseObject = searchIndexService.search(COMPANY_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_FOUND, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns an error")
    void searchRequestReturnsError() throws Exception {
        when(mockDissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID))
                .thenThrow(SearchException.class);

        DissolvedResponseObject responseObject = searchIndexService.search(COMPANY_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_ERROR, responseObject.getStatus());
    }

    @Test
    @DisplayName("Test search returns no results")
    void searchRequestReturnsNoResults() throws Exception {
        when(mockDissolvedSearchRequestService.getSearchResults(COMPANY_NAME, REQUEST_ID))
                .thenReturn(createSearchResults(false));
        DissolvedResponseObject responseObject = searchIndexService.search(COMPANY_NAME, REQUEST_ID);

        assertNotNull(responseObject);
        assertEquals(ResponseStatus.SEARCH_NOT_FOUND, responseObject.getStatus());
    }


    private DissolvedSearchResults createSearchResults(boolean isResultsPopulated) {
        DissolvedSearchResults searchResults = new DissolvedSearchResults();
        TopHit topHit = new TopHit();
        topHit.setCompanyName("companyName");
        topHit.setCompanyNumber("00000000");
        searchResults.setTopHit(topHit);
        searchResults.setEtag("etag");
        if (isResultsPopulated) {
            searchResults.setItems(createItems());
        }
        return searchResults;
    }
    private List<DissolvedCompany> createItems() {
        List<DissolvedCompany> items = new ArrayList<>();
        DissolvedCompany dissolvedCompany = new DissolvedCompany();
        Address address = new Address();
        address.setLocality(LOCALITY);
        address.setPostalCode(POSTAL_CODE);
        PreviousCompanyName previousCompanyName = new PreviousCompanyName();
        previousCompanyName.setDateOfNameCessation(DATE_OF_NAME_CESSATION);
        previousCompanyName.setDateOfNameEffectiveness(DATE_OF_NAME_EFFECTIVENESS);
        previousCompanyName.setName(PREVIOUS_NAME);
        List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
        previousCompanyNames.add(previousCompanyName);
        dissolvedCompany.setCompanyName(COMPANY_NAME);
        dissolvedCompany.setCompanyNumber(COMPANY_NUMBER);
        dissolvedCompany.setCompanyStatus(COMPANY_STATUS);
        dissolvedCompany.setDateOfCessation(DATE_OF_CESSATION);
        dissolvedCompany.setDateOfCreation(DATE_OF_CREATION);
        dissolvedCompany.setAddress(address);
        dissolvedCompany.setPreviousCompanyNames(previousCompanyNames);
        items.add(dissolvedCompany);
        return items;
    }
}
