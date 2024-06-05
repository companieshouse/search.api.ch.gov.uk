package java.uk.gov.companieshouse.search.api.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimarySearchSteps {

    private ResponseEntity<String> lastResponse;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Given("Company profile api service is running")
    public void theApplicationRunning() {
        assertThat(restTemplate).isNotNull();
        lastResponse = null;
    }

    @When("the client invokes {string} endpoint")
    public void theClientInvokesAnEndpoint(String url) {
        lastResponse = restTemplate.getForEntity(url, String.class);
    }

    @Then("the client receives a status code of {int}")
    public void theClientReceivesStatusCodeOf(int code) {
        assertThat(lastResponse.getStatusCode()).isEqualTo(HttpStatus.valueOf(code));
    }

    @And("the client receives a response body of {string}")
    public void theClientReceivesRawResponse(String response) {
        assertThat(lastResponse.getBody()).isEqualTo(response);
    }

    @Given("the company search entity resource {string} exists for {string}")
    public void theCompanySearchEntityResourceExistsFor(String arg0, String arg1) {
    }

    @When("a DELETE request is sent to the company search endpoint for {string}")
    public void aDELETERequestIsSentToTheCompanySearchEndpointFor(String arg0) {
        
    }

    @And("the company search entity does not exist for {string}")
    public void theCompanySearchEntityDoesNotExistFor(String arg0) {
        
    }

    @Then("I should receive {int} status code")
    public void iShouldReceiveStatusCode(int arg0) {
        
    }

    @Then("the response code should be {int}")
    public void theResponseCodeShouldBe(int arg0) {

    }
}
