package uk.gov.companieshouse.search.api.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.EndpointException;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class ElasticSearchConfig {

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String ELASTIC_SEARCH_URL = "ELASTIC_SEARCH_URL";

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {

        URL endpoint;
        try {
            endpoint = new URL(environmentReader.getMandatoryString(ELASTIC_SEARCH_URL));
        } catch (MalformedURLException e) {
            throw new EndpointException(ELASTIC_SEARCH_URL + " environment variable is malformed; expected format is <protocol>://<host>[:port]");
        }

        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(endpoint.getHost(), endpoint.getPort(), endpoint.getProtocol())));
    }
}
