package uk.gov.companieshouse.search.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class AlphaKeyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String ALPHAKEY_SERVICE_URL = "ALPHAKEY_SERVICE_URL";

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    public AlphaKeyResponse getAlphaKeyForCorporateName(String corporateName){
        String alphaKeyUrl = environmentReader.getMandatoryString(ALPHAKEY_SERVICE_URL) + corporateName;

        try {
            ResponseEntity<AlphaKeyResponse> response =
                restTemplate.getForEntity(alphaKeyUrl, AlphaKeyResponse.class);

            return response.getBody();
        } catch (RestClientException e) {
            LOG.error("Error occurred during api call to alphakey service");
        }
        return null;
    }

}
