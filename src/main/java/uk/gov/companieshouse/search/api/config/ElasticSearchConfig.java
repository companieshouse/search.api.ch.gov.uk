package uk.gov.companieshouse.search.api.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost("es7-search-server1-cai.aws.chdev.org", 9200, "http")));
    }
}
