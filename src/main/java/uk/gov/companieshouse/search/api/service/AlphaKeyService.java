package uk.gov.companieshouse.search.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import java.util.Map;

@Service
public class AlphaKeyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String ALPHAKEY_SERVICE_URL = "ALPHAKEY_SERVICE_URL";

    public AlphaKeyResponse getAlphaKeyForCorporateName(String corporateName){
        String alphaKeyUrl = environmentReader.getMandatoryString(ALPHAKEY_SERVICE_URL) + corporateName.replace("&", "AND");

        Map<String, Object> logMap =  new DataMap.Builder()
                .companyName(corporateName)
                .build().getLogMap();

        LoggingUtils.getLogger().info("Getting alphakey from alphakey service", logMap);

        try {
            ResponseEntity<AlphaKeyResponse> response =
                restTemplate.getForEntity(alphaKeyUrl, AlphaKeyResponse.class);

            return response.getBody();
        } catch (RestClientException e) {
            LoggingUtils.getLogger().error("Error occurred during api call to alphakey service", logMap);
        }
        return null;
    }

    public AlphaKeyResponse getAlphaKeyForOfficer(String officerCorporateName){
        String alphaKeyUrl = environmentReader.getMandatoryString(ALPHAKEY_SERVICE_URL) + officerCorporateName.replace("&", "AND");

        Map<String, Object> logMap =  new DataMap.Builder()
                .companyName(officerCorporateName)
                .build().getLogMap();

        LoggingUtils.getLogger().info("Getting alphakey from alphakey service", logMap);

        try{
            ResponseEntity<AlphaKeyResponse> response =
                    restTemplate.getForEntity(alphaKeyUrl, AlphaKeyResponse.class);
            return response.getBody();
        } catch (RestClientException ex) {
            LoggingUtils.getLogger().error("Error occurred during api call to alphakey service", logMap);
        }
        return null;
    }
}