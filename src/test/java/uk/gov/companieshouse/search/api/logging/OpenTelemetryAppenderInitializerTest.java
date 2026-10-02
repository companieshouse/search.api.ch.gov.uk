package uk.gov.companieshouse.search.api.logging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class OpenTelemetryAppenderInitializerTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(OpenTelemetryAppenderInitializer.class);

    @Test
    void afterPropertiesSetInstallsAppender() {
        OpenTelemetry openTelemetry = mock(OpenTelemetry.class);
        OpenTelemetryAppenderInitializer initializer =
                new OpenTelemetryAppenderInitializer(openTelemetry);

        assertDoesNotThrow(initializer::afterPropertiesSet);
    }

    @Test
    void beanIsNotCreatedWhenPropertyIsDisabled() {
        contextRunner.run(context ->
                assertThat(context).doesNotHaveBean(OpenTelemetryAppenderInitializer.class));
    }

    @Test
    void beanIsCreatedWhenPropertyIsEnabled() {
        contextRunner
                .withPropertyValues("management.opentelemetry.enabled=true")
                .withBean(OpenTelemetry.class, () -> mock(OpenTelemetry.class))
                .run(context -> assertThat(context).hasSingleBean(OpenTelemetryAppenderInitializer.class));
    }
}

