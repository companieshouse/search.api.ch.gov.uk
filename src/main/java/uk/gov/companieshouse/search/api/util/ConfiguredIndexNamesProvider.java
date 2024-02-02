package uk.gov.companieshouse.search.api.util;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Component
public class ConfiguredIndexNamesProvider {

    private static final String ALPHABETICAL_SEARCH_INDEX_ENVIRONMENT_VARIABLE = "ALPHABETICAL_SEARCH_INDEX";
    private static final String DISSOLVED_SEARCH_INDEX_ENVIRONMENT_VARIABLE = "DISSOLVED_SEARCH_INDEX";
    private static final String ADVANCED_SEARCH_INDEX_ENVIRONMENT_VARIABLE = "ADVANCED_SEARCH_INDEX";
    private static final String PRIMARY_SEARCH_INDEX_ENVIRONMENT_VARIABLE = "PRIMARY_SEARCH_INDEX";

    private final EnvironmentReader environment;

    public ConfiguredIndexNamesProvider(EnvironmentReader environment) {
        this.environment = environment;
    }

    /**
     * @return the value of the {@link #ALPHABETICAL_SEARCH_INDEX_ENVIRONMENT_VARIABLE} environment variable
     */
    public String alphabetical() {
        return environment.getMandatoryString(ALPHABETICAL_SEARCH_INDEX_ENVIRONMENT_VARIABLE);
    }

    /**
     * @return the value of the {@link #DISSOLVED_SEARCH_INDEX_ENVIRONMENT_VARIABLE} environment variable
     */
    public String dissolved() {
        return environment.getMandatoryString(DISSOLVED_SEARCH_INDEX_ENVIRONMENT_VARIABLE);
    }

    /**
     * @return the value of the {@link #ADVANCED_SEARCH_INDEX_ENVIRONMENT_VARIABLE} environment variable
     */
    public String advanced() {
        return environment.getMandatoryString(ADVANCED_SEARCH_INDEX_ENVIRONMENT_VARIABLE);
    }

    /**
     * @return the value of the {@link #PRIMARY_SEARCH_INDEX_ENVIRONMENT_VARIABLE} environment variable
     */
    public String primary() {
        return environment.getMandatoryString(PRIMARY_SEARCH_INDEX_ENVIRONMENT_VARIABLE);
    }

}
