package uk.gov.companieshouse.search.api.service.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class AlphaKeyService {

    @Autowired
    private RestTemplate restTemplate;


    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    public AlphaKeyResponse getAlphaKeyForCorporateName(String corpName){
        String url = "http://internal-kermit-chs-alphakey-860414726.eu-west-1.elb.amazonaws.com/alphakey?name="+corpName;

        try {
            ResponseEntity<AlphaKeyResponse> response = restTemplate.getForEntity(url, AlphaKeyResponse.class);

            return response.getBody();
        } catch (Exception e) {
            LOG.error("Error occurred during api call to alphakey service");
        }

        return null;
    }

}
