package uk.gov.companieshouse.search.api.config;

import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.EndpointException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ElasticSearchConfigTest {

    @InjectMocks
    private ElasticSearchConfig elasticSearchConfig;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    private static final String ENV_READER_RESULT_ALPHABETICAL = "https://cluster-alphabetical.url.com";
    private static final String ENV_READER_RESULT_DISSOLVED = "https://cluster-dissolved.url.com";

    @Test
    @DisplayName("Test calling create client returns a rest high level client for alphabetical search")
    void testAlphabeticalRestClient() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT_ALPHABETICAL);

        RestHighLevelClient restHighLevelClient = elasticSearchConfig.alphabeticalRestClient();

        assertNotNull(restHighLevelClient);

        List<Node> nodes = restHighLevelClient.getLowLevelClient().getNodes();
        assertEquals(ENV_READER_RESULT_ALPHABETICAL, nodes.get(0).getHost().toString());
        assertEquals(1, restHighLevelClient.getLowLevelClient().getNodes().size());
    }

    @Test
    @DisplayName("Test calling create client returns a rest high level client for dissolved search")
    void testDissolvedRestClient() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT_DISSOLVED);

        RestHighLevelClient restHighLevelClient = elasticSearchConfig.dissolvedRestClient();
        assertNotNull(restHighLevelClient);

        List<Node> nodes = restHighLevelClient.getLowLevelClient().getNodes();
        assertEquals(ENV_READER_RESULT_DISSOLVED, nodes.get(0).getHost().toString());
        assertEquals(1, restHighLevelClient.getLowLevelClient().getNodes().size());
    }

    @Test
    @DisplayName("Test create rest high level client returns object successfully")
    void createRestHighLevelClient() throws Exception {

        when(mockEnvironmentReader.getMandatoryString(anyString())).thenReturn(ENV_READER_RESULT_ALPHABETICAL);

        RestHighLevelClient restHighLevelClient = elasticSearchConfig.createClient(ENV_READER_RESULT_ALPHABETICAL);

        assertNotNull(restHighLevelClient);

        List<Node> nodes = restHighLevelClient.getLowLevelClient().getNodes();
        assertEquals(ENV_READER_RESULT_ALPHABETICAL, nodes.get(0).getHost().toString());
        assertEquals(1, restHighLevelClient.getLowLevelClient().getNodes().size());
    }

    @Test
    @DisplayName("Test create rest high level client throws endpoint exception when not passed valid url")
    void createRestHighLevelClientThrowsEndpointException() throws Exception {

        assertThrows(EndpointException.class, () ->
                elasticSearchConfig.createClient("test"));
    }
}