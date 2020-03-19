package uk.gov.companieshouse.search.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.environment.EnvironmentReader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SearchApiApplicationConfigurationTest {

    @Test
    @DisplayName("Test environment reader return new EnvironmentReaderImpl")
    void testEnvironmentReaderReturnsSuccessfully() {

        SearchApiApplicationConfiguration searchApiApplicationConfiguration
            = new SearchApiApplicationConfiguration();

        EnvironmentReader environmentReader = searchApiApplicationConfiguration.environmentReader();
        assertNotNull(environmentReader);
    }

    @Test
    @DisplayName("Test rest template return new RestTemplate")
    void testRestTemplateReturnsSuccessfully() {

        SearchApiApplicationConfiguration searchApiApplicationConfiguration
            = new SearchApiApplicationConfiguration();

        RestTemplate restTemplate = searchApiApplicationConfiguration.restTemplate();
        assertNotNull(restTemplate);
    }
}
