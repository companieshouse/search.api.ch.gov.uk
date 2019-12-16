package uk.gov.companieshouse.search.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

@Configuration
public class SearchApiApplicationConfiguration {

    @Bean
    EnvironmentReader environmentReader() {
        return new EnvironmentReaderImpl();
    }
}
