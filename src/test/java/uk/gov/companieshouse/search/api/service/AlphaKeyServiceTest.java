package uk.gov.companieshouse.search.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlphaKeyServiceTest {

    @InjectMocks
    private AlphaKeyService alphaKeyService;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    private static final String SAME_AS_ALPHA_KEY = "sameAsAlphaKey";
    private static final String ORDERED_ALPHA_KEY= "orderedAlphaKey";
    private static final String UPPERCASE_NAME = "upperCaseName";
    private static final String CORPORATE_NAME = "corporateName";
    private static final String URL = "url";

    @Test
    @DisplayName("Test alpha key response returned successfully")
    void testAlphaKeyResponseSuccessful() {

        ResponseEntity<AlphaKeyResponse> response = new ResponseEntity<>(createAlphaKeyResponse(), HttpStatus.OK);

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(URL);

        when(mockRestTemplate.getForEntity(anyString(), eq(AlphaKeyResponse.class))).thenReturn(response);

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME);

        assertNotNull(alphaKeyResponse);
        assertEquals(alphaKeyResponse.getSameAsAlphaKey(), SAME_AS_ALPHA_KEY);
        assertEquals(alphaKeyResponse.getOrderedAlphaKey(), ORDERED_ALPHA_KEY);
        assertEquals(alphaKeyResponse.getUpperCaseName(), UPPERCASE_NAME);
    }

    @Test
    @DisplayName("Test alpha key response throws exception")
    void testAlphaKeyResponseThrowsException() {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(URL);

        when(mockRestTemplate.getForEntity(anyString(), eq(AlphaKeyResponse.class)))
            .thenThrow(RestClientException.class);

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME);
        assertNull(alphaKeyResponse);

    }

    private AlphaKeyResponse createAlphaKeyResponse() {
        AlphaKeyResponse alphaKeyResponse = new AlphaKeyResponse();

        alphaKeyResponse.setSameAsAlphaKey(SAME_AS_ALPHA_KEY);
        alphaKeyResponse.setOrderedAlphaKey(ORDERED_ALPHA_KEY);
        alphaKeyResponse.setUpperCaseName(UPPERCASE_NAME);

        return alphaKeyResponse;
    }
}
