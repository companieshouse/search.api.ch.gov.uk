package uk.gov.companieshouse.search.api.config;

import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5Transport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.EndpointException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Configuration
public class OpenSearchConfig {

    private final EnvironmentReader environmentReader;

    public OpenSearchConfig(EnvironmentReader environmentReader) {
        this.environmentReader = environmentReader;
    }

    // These are currently pointing at the existing OS instance, will need to be updated in the configs for both
    private static final String ALPHABETICAL_SEARCH_URL = "OPEN_SEARCH_URL";

    @Bean
    public OpenSearchClient alphabeticalOpenSearchRestClient() {
        return createOpenSearchClient(ALPHABETICAL_SEARCH_URL);
    }

    public OpenSearchClient createOpenSearchClient(String url) {
        URL endpoint;

        try {
            String rawUrl = environmentReader.getMandatoryString(url);
            URI uri = new URI(rawUrl);
            endpoint = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new EndpointException(
                    url + " environment variable is malformed; expected format is <protocol>://<host>[:port]"
            );
        }

        HttpHost httpHost = new HttpHost(endpoint.getHost(), endpoint.getPort());
        ApacheHttpClient5Transport transport = ApacheHttpClient5TransportBuilder
                .builder(httpHost)
                .setMapper(new JacksonJsonpMapper())
                .setHttpClientConfigCallback(
                        HttpAsyncClientBuilder::disableContentCompression
                )
                .build();

        return new OpenSearchClient(transport);
    }
}
