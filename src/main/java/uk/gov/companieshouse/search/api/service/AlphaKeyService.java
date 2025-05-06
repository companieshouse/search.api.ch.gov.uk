package uk.gov.companieshouse.search.api.service;

import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;

@Service
public class AlphaKeyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String ALPHAKEY_SERVICE_URL = "ALPHAKEY_SERVICE_URL";

    public AlphaKeyResponse getAlphaKeyForCorporateName(String corporateName) {
        corporateName = corporateName.replace("&", "AND");
        Map<String, Object> logMap = new DataMap.Builder()
                .companyName(corporateName)
                .build().getLogMap();

        final String alphaKeyUrl = environmentReader.getMandatoryString(ALPHAKEY_SERVICE_URL);

        URI uri = UriComponentsBuilder.fromUriString(alphaKeyUrl)
                .queryParam("name", corporateName)
                .build()
                .encode()
                .toUri();

        LoggingUtils.getLogger().info("Getting alphakey from alphakey service", logMap);

        try {
            return restTemplate.getForObject(uri, AlphaKeyResponse.class);
        } catch (RestClientException e) {
            LoggingUtils.getLogger().error("Error occurred during api call to alphakey service", logMap);
        }
        return null;
    }
}