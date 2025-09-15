package uk.gov.companieshouse.search.api.controller;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchRequests;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.service.delete.alphabetical.AlphabeticalSearchDeleteService;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@SpringBootTest
@AutoConfigureMockMvc
class AlphabeticalSearchControllerCORSTest {

    private static final String GET_ALPHABETICAL_SEARCH = "/alphabetical-search/companies?q=";
    private static final String PUT_ALPHABETICAL_SEARCH = "/alphabetical-search/companies/00006400";

    private static final String X_REQUEST_ID = "123456";
    private static final String ERIC_IDENTITY = "Test-Identity";
    private static final String ERIC_IDENTITY_TYPE = "key";
    private static final String ERIC_PRIVILEGES = "*";
    private static final String ERIC_AUTH = "internal-app";

    @MockitoBean
    private EnvironmentReader mockEnvironmentReader;

    @MockitoBean
    AlphabeticalSearchRequests alphabeticalSearchRequests;

    @MockitoBean
    private RestHighLevelClient advancedRestClient;

    @MockitoBean
    private RestHighLevelClient alphabeticalRestClient;

    @MockitoBean
    private RestHighLevelClient dissolvedRestClient;

    @MockitoBean
    private RestHighLevelClient primaryRestClient;

    @MockitoBean
    private AdvancedSearchRestClientService advancedRestClientS;

    @MockitoBean
    private AlphabeticalSearchRestClientService alphabeticalSearchRestClientService;

    // Injected services

    @MockitoBean
    private AlphabeticalSearchIndexService searchIndexService;

    @MockitoBean
    private ApiToResponseMapper apiToResponseMapper;

    @MockitoBean
    private UpsertCompanyService upsertCompanyService;

    @MockitoBean
    private ConfiguredIndexNamesProvider indices;

    @MockitoBean
    private AlphabeticalSearchDeleteService alphabeticalSearchDeleteService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlphabeticalSearchController alphabeticalSearchController;

    @Test
    void optionsAlphabeticalSearchCORS() throws Exception {

        mockMvc.perform(options(GET_ALPHABETICAL_SEARCH)
                        .header("Origin", "")
                        .contentType(APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS))
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_MAX_AGE));
    }

    @Test
    void getAlphabeticalSearchCORS() throws Exception {

        mockMvc.perform(get(GET_ALPHABETICAL_SEARCH)
                        .header("Origin", "")
                        .header("ERIC-Allowed-Origin", "some-origin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("ERIC-Identity", ERIC_IDENTITY)
                        .header("ERIC-Identity-Type", ERIC_IDENTITY_TYPE)
                        .header("x-request-id", X_REQUEST_ID)
                        )
            .andExpect(status().isOk())
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("GET")));
    }

    @Test
    void getAlphabeticalSearchCORSForbidden() throws Exception {

        mockMvc.perform(get(GET_ALPHABETICAL_SEARCH)
                        .header("Origin", "")
                        .header("ERIC-Allowed-Origin", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("ERIC-Identity", ERIC_IDENTITY)
                        .header("ERIC-Identity-Type", ERIC_IDENTITY_TYPE)
                        .header("x-request-id", X_REQUEST_ID))
            .andExpect(status().isForbidden())
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("GET")));
    }

    @Test
    void putAlphabeticalSearchCORSForbidden() throws Exception {

        mockMvc.perform(put(PUT_ALPHABETICAL_SEARCH)
                        .header("Origin", "")
                        .header("ERIC-Allowed-Origin", "some-origin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("ERIC-Identity", ERIC_IDENTITY)
                        .header("ERIC-Identity-Type", ERIC_IDENTITY_TYPE)
                        .header("x-request-id", X_REQUEST_ID))
            .andExpect(status().isForbidden())
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("GET")));
    }

}
