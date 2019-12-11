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
                // TODO update to use Environment reader and place host name in chs-configs PCI-415
                new HttpHost("es7-search-server1-shaun.aws.chdev.org", 9200, "http")));
    }
}
