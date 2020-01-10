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

    private static final String END_POINT = "END_POINT";

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() throws EndpointException {

        URL endpoint;
        try {
            endpoint = new URL(environmentReader.getMandatoryString(END_POINT));
        } catch (MalformedURLException e) {
            throw new EndpointException("A malformed url has occurred - url may have failed to parse or invalid protocol provided");
        }

        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(endpoint.getHost(), endpoint.getPort(), endpoint.getProtocol())));
    }
}
