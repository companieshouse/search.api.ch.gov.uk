package uk.gov.companieshouse.search.api.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HealthCheckControllerTest {
    
    @InjectMocks
    private HealthCheckController testClass;
    
    @Test
    @DisplayName("Health check confirms health with HTTP 200")
    public void applicationHealthcheckRunsSuccessfully(){
        // When the health endpoint is polled
        final ResponseEntity<Void> response = testClass.getHealthCheck();
        // Then the response is HTTP 200
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
