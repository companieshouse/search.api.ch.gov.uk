package uk.gov.companieshouse.search.api.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Configuration
public class ElasticSearchConfig {

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String HOST_NAME = "SEARCH_API_HOST";

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(environmentReader.getMandatoryString(HOST_NAME))));
    }
}
