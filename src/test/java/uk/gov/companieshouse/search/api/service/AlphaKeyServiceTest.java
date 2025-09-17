package uk.gov.companieshouse.search.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;

@ExtendWith(MockitoExtension.class)
class AlphaKeyServiceTest {

    @InjectMocks
    private AlphaKeyService alphaKeyService;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    private static final String SAME_AS_ALPHA_KEY = "sameAsAlphaKey";
    private static final String ORDERED_ALPHA_KEY = "orderedAlphaKey";
    private static final String UPPERCASE_NAME = "upperCaseName";
    private static final String CORPORATE_NAME = "corporateName";
    private static final String URL = "url";

    @Test
    @DisplayName("Test alpha key response returned successfully")
    void testAlphaKeyResponseSuccessful() {
        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(URL);
        when(mockRestTemplate.getForObject(any(), eq(AlphaKeyResponse.class))).thenReturn(createAlphaKeyResponse());
        URI uri = UriComponentsBuilder.fromUriString(URL)
                .queryParam("name", CORPORATE_NAME)
                .build()
                .encode()
                .toUri();

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME);

        assertNotNull(alphaKeyResponse);
        assertEquals(SAME_AS_ALPHA_KEY, alphaKeyResponse.getSameAsAlphaKey());
        assertEquals(ORDERED_ALPHA_KEY, alphaKeyResponse.getOrderedAlphaKey());
        assertEquals(UPPERCASE_NAME, alphaKeyResponse.getUpperCaseName());
        verify(mockRestTemplate).getForObject(uri, AlphaKeyResponse.class);
    }

    @Test
    @DisplayName("Test alpha key response not returned due to rest client exception")
    void testNoResponseReturnedDueToException() {
        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(URL);
        when(mockRestTemplate.getForObject(any(), eq(AlphaKeyResponse.class)))
                .thenThrow(new RestClientException("error"));

        URI uri = UriComponentsBuilder.fromUriString(URL)
                .queryParam("name", CORPORATE_NAME)
                .build()
                .encode()
                .toUri();

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(CORPORATE_NAME);
        assertNull(alphaKeyResponse);
        verify(mockRestTemplate).getForObject(uri, AlphaKeyResponse.class);
    }

    private AlphaKeyResponse createAlphaKeyResponse() {
        AlphaKeyResponse alphaKeyResponse = new AlphaKeyResponse();

        alphaKeyResponse.setSameAsAlphaKey(SAME_AS_ALPHA_KEY);
        alphaKeyResponse.setOrderedAlphaKey(ORDERED_ALPHA_KEY);
        alphaKeyResponse.setUpperCaseName(UPPERCASE_NAME);

        return alphaKeyResponse;
    }
}
