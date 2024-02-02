package uk.gov.companieshouse.search.api.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
class ConfiguredIndexNamesProviderTest {

    @InjectMocks
    private ConfiguredIndexNamesProvider indices;

    @Mock
    private EnvironmentReader environment;

    @Test
    @DisplayName("Alphabetical index name got from ALPHABETICAL_SEARCH_INDEX environment variable")
    void alphabeticalGetsCorrectIndexName() {
        assertExpectedIndexNameGot(() -> indices.alphabetical(), "ALPHABETICAL_SEARCH_INDEX");
    }

    @Test
    @DisplayName("Dissolved index name got from DISSOLVED_SEARCH_INDEX environment variable")
    void dissolvedGetsCorrectIndexName() {
        assertExpectedIndexNameGot(() -> indices.dissolved(), "DISSOLVED_SEARCH_INDEX");
    }

    @Test
    @DisplayName("Advanced index name got from ADVANCED_SEARCH_INDEX environment variable")
    void advancedGetsCorrectIndexName() {
        assertExpectedIndexNameGot(() -> indices.advanced(), "ADVANCED_SEARCH_INDEX");
    }

    @Test
    @DisplayName("Primary index name got from PRIMARY_SEARCH_INDEX environment variable")
    void primaryGetsCorrectIndexName() {
        assertExpectedIndexNameGot(() -> indices.primary(), "PRIMARY_SEARCH_INDEX");
    }

    interface StringValueProvider {
        String getValue();
    }

    void assertExpectedIndexNameGot(
        final StringValueProvider provider,
        final String indexEnvironmentVariableName) {

        // Given
        when(environment.getMandatoryString(indexEnvironmentVariableName)).thenReturn("index name");

        // When and then
        assertThat(provider.getValue(), is("index name"));
    }
}