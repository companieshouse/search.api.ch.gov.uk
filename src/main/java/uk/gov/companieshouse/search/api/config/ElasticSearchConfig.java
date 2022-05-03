package uk.gov.companieshouse.search.api.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.EndpointException;

@Configuration
public class ElasticSearchConfig {

    @Autowired
    private EnvironmentReader environmentReader;

    // These are currently pointing at the existing ES instance, will need to be updated in the configs for both
    private static final String ALPHABETICAL_SEARCH_URL = "ELASTIC_SEARCH_URL";
    private static final String DISSOLVED_SEARCH_URL = "DISSOLVED_SEARCH_URL";
    private static final String ADVANCED_SEARCH_URL = "ADVANCED_SEARCH_URL";
    private static final String DISQUALIFIED_SEARCH_URL = "DISQUALIFIED_SEARCH_URL";

    @Qualifier("alphabeticalClient")
    @Bean(destroyMethod = "close")
    public RestHighLevelClient alphabeticalRestClient() {
        return createClient(ALPHABETICAL_SEARCH_URL);
    }

    @Qualifier("advancedClient")
    @Bean(destroyMethod = "close")
    public RestHighLevelClient advancedRestClient() {
        return createClient(ADVANCED_SEARCH_URL);
    }

    @Qualifier("dissolvedClient")
    @Bean(destroyMethod = "close")
    public RestHighLevelClient dissolvedRestClient() {
        return createClient(DISSOLVED_SEARCH_URL);
    }

    @Qualifier("disqualifiedClient")
    @Bean(destroyMethod = "close")
    public RestHighLevelClient disqualifiedRestClient() {
        return createClient(DISQUALIFIED_SEARCH_URL);
    }

    public RestHighLevelClient createClient(String url) {

        URL endpoint;
        try {
            endpoint = new URL(environmentReader.getMandatoryString(url));
        } catch (MalformedURLException e) {
            throw new EndpointException(url + " environment variable is malformed; expected format is <protocol>://<host>[:port]");
        }

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(endpoint.getHost(), endpoint.getPort(), endpoint.getProtocol())));
    }
}