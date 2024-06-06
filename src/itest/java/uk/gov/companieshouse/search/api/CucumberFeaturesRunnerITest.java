package java.uk.gov.companieshouse.search.api;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import java.uk.gov.companieshouse.search.api.config.AbstractIntegrationTest;


@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/itest/resources/features",
        plugin = {"pretty", "json:target/cucumber-report.json"})
@CucumberContextConfiguration
@TestPropertySource(properties = {"mongodb.transactional = true"})
public class CucumberFeaturesRunnerITest extends AbstractIntegrationTest {
}
