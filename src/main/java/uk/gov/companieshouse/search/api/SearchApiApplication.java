package uk.gov.companieshouse.search.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.search.api.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.search.api.interceptor.UserAuthorisationInterceptor;

@SpringBootApplication
public class SearchApiApplication implements WebMvcConfigurer {

    public static final String APPLICATION_NAME_SPACE = "search.api.ch.gov.uk";

    @Autowired
    private UserAuthorisationInterceptor authorisationInterceptor;

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    public static void main(String[] args) {
        SpringApplication.run(SearchApiApplication.class, args);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor).excludePathPatterns("/search/healthcheck");
        registry.addInterceptor(authorisationInterceptor).addPathPatterns(
                "/advanced-search/companies/{company_number}", 
                "/alphabetical-search/companies/{company_number}",
                "/disqualified-search/disqualified-officers/{officer_id}",
                "/disqualified-search/delete/{officer_id}");
    }
}
