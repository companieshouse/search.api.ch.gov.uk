package java.uk.gov.companieshouse.search.api.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.index.VersionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.companieshouse.api.company.Data;
import java.uk.gov.companieshouse.search.api.config.CucumberContext;
import uk.gov.companieshouse.search.api.controller.PrimarySearchController;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;


import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@CucumberContextConfiguration
@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
public class PrimarySearchSteps {

    private ResponseEntity<String> lastResponse;

    private String contextId;

    private MockMvc mockMvc;

    @Autowired
    private PrimarySearchRestClientService primarySearchRestClientService;

    @Autowired
    protected TestRestTemplate restTemplate;

    private ConfiguredIndexNamesProvider indices;

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
    public void aDELETERequestIsSentToTheCompanySearchEndpointFor(String companyNumber) {
        String uri = "primary-search/companies/{company_number}";

        HttpHeaders headers = new HttpHeaders();
        headers.add("ERIC-Identity", "SOME_IDENTITY");
        headers.add("ERIC-Identity-Type", "key");
        headers.add("x-request-id", "123456");
        headers.add("ERIC-Authorised-Key-Privileges", "internal-app");

        ResponseEntity<Data> response = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(headers),
                Data.class, companyNumber);

        CucumberContext.CONTEXT.set("statusCode", response.getStatusCodeValue());
        CucumberContext.CONTEXT.set("getResponseBody", response.getBody());
        
    }

    @And("the company search entity does not exist for {string}")
    public void theCompanySearchEntityDoesNotExistFor(String arg0) {

        
    }

    @Then("I should receive {int} status code")
    public void iShouldReceiveStatusCode(int statusCode) {
        Integer expectedStatusCode = CucumberContext.CONTEXT.get("statusCode");
        Assertions.assertThat(expectedStatusCode).isEqualTo(statusCode);
        
    }

    @When("a DELETE request is sent to the primary search endpoint for {string} without valid ERIC headers")
    public void aDELETERequestIsSentToThePrimarySearchEndpointForWithoutValidERICHeaders(String companyNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        this.contextId = "5234234234";
        CucumberContext.CONTEXT.set("contextId", this.contextId);
        headers.set("x-request-id", this.contextId);

        HttpEntity<String> request = new HttpEntity<String>(null, headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                "primary-search/companies/{company_number}/", HttpMethod.DELETE, request, Void.class, companyNumber);
        CucumberContext.CONTEXT.set("statusCode", response.getStatusCode().value());
    }

    @When("a DELETE request is sent to the primary search endpoint for {string} with insufficient access")
    public void aDELETERequestIsSentToThePrimarySearchEndpointForWithInsufficientAccess(String companyNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        this.contextId = "5234234234";
        CucumberContext.CONTEXT.set("contextId", this.contextId);
        headers.set("x-request-id", this.contextId);

        headers.set("ERIC-Identity", "TEST-IDENTITY");
        headers.set("ERIC-Identity-Type", "key");
        headers.set("ERIC-Authorised-Key-Roles", "basic-role");
        headers.add("api-key", "g9yZIA81Zo9J46Kzp3JPbfld6kOqxR47EAYqXbRV");
        headers.add("ERIC-Authorised-Key-Privileges", "");
        headers.set("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                "primary-search/companies/{company_number}", HttpMethod.DELETE, request, Void.class, companyNumber);
        CucumberContext.CONTEXT.set("statusCode", response.getStatusCode().value());
    }

    @Given("a company profile resource does not exist for {string}")
    public void aCompanyProfileResourceDoesNotExistFor(String arg0) {

    }

    @And("the company profile database is down")
    public void theCompanyProfileDatabaseIsDown() {

    }


    @Given("the company search entity resource exists for {string}")
    public void theCompanySearchEntityResourceExistsFor(String companyNumber) throws Exception {
        //when(primarySearchRestClientService.delete(any())).thenReturn(new DeleteResponse(null, VersionType.INTERNAL,-2L,0L,indices.primary(),companyNumber,true));
    }
}
