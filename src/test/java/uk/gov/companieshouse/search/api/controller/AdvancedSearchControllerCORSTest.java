package uk.gov.companieshouse.search.api.controller;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchRequests;
import uk.gov.companieshouse.search.api.mapper.AdvancedQueryParamMapper;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.service.delete.advanced.AdvancedSearchDeleteService;
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchIndexService;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@SpringBootTest
@AutoConfigureMockMvc
class AdvancedSearchControllerCORSTest {

    private static final String GET_ADVANCED_SEARCH = "/advanced-search/companies";
    private static final String PUT_ADVANCED_SEARCH = "/advanced-search/companies/00006400";

    private static final String X_REQUEST_ID = "123456";
    private static final String ERIC_IDENTITY = "Test-Identity";
    private static final String ERIC_IDENTITY_TYPE = "key";
    private static final String ERIC_PRIVILEGES = "*";
    private static final String ERIC_AUTH = "internal-app";

    @MockBean
    private EnvironmentReader mockEnvironmentReader;

    @MockBean
    AlphabeticalSearchRequests alphabeticalSearchRequests;

    @MockBean
    @Qualifier("advancedClient")
    private RestHighLevelClient advancedClient;

    @MockBean
    @Qualifier("alphabeticalClient")
    private RestHighLevelClient alphabeticalClient;

    @MockBean
    @Qualifier("dissolvedClient")
    private RestHighLevelClient dissolvedClient;

    @MockBean
    @Qualifier("primaryClient")
    private RestHighLevelClient primaryClient;

    // Injected services

    @MockBean
    private AdvancedQueryParamMapper queryParamMapper;

    @MockBean
    private AdvancedSearchIndexService searchIndexService;

    @MockBean
    private ApiToResponseMapper apiToResponseMapper;

    @MockBean
    private UpsertCompanyService upsertCompanyService;

    @MockBean
    private ConfiguredIndexNamesProvider indices;

    @MockBean
    private AdvancedSearchDeleteService advancedSearchDeleteService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdvancedSearchController advancedSearchController;

    @Test
    void optionsAdvancedSearchCORS() throws Exception {

        mockMvc.perform(options(GET_ADVANCED_SEARCH)
                        .header("Origin", "")
                        .contentType(APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS))
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))
            .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_MAX_AGE));
    }

    @Test
    void getAdvancedSearchCORS() throws Exception {

        mockMvc.perform(get(GET_ADVANCED_SEARCH)
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
    void getAdvancedSearchCORSForbidden() throws Exception {

        mockMvc.perform(get(GET_ADVANCED_SEARCH)
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
    void putCompanyFilingHistoryListCORSForbidden() throws Exception {

        mockMvc.perform(put(PUT_ADVANCED_SEARCH)
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
