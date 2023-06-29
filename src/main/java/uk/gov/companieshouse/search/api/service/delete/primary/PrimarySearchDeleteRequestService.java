package uk.gov.companieshouse.search.api.service.delete.primary;

import org.elasticsearch.action.delete.DeleteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchType;

import java.util.Map;

@Service
public class PrimarySearchDeleteRequestService {
    private static final String INDEX = "PRIMARY_SEARCH_INDEX";
    private static final String TYPE = "primary_search";
    @Autowired
    private EnvironmentReader environmentReader;

    public DeleteRequest createDeleteRequest(SearchType searchType) {

        Map<String, Object> logMap = LoggingUtils.setUpPrimarySearchDeleteLogging(searchType.getOfficerId());
        String index = environmentReader.getMandatoryString(INDEX);

        DeleteRequest request = new DeleteRequest(index, TYPE, searchType.getOfficerId());

        LoggingUtils.getLogger().info(String.format("Attempting to delete an [%s] with given id: [%s] from primary search index",
                searchType.getPrimarySearchType(), searchType.getOfficerId()), logMap);

        return request;
    }
}
