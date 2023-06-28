package uk.gov.companieshouse.search.api.service.delete.officers;

import org.elasticsearch.action.delete.DeleteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;

import java.util.Map;

public class OfficerDeleteRequestService {

    private static final String INDEX = "PRIMARY_SEARCH_INDEX";
    private static final String TYPE = "primary_search";
    @Autowired
    private EnvironmentReader environmentReader;
    public DeleteRequest createDeleteRequest(String officerId) {
        Map<String, Object> logMap = LoggingUtils.setUpOfficersDeleteLogging(officerId);
        String index = environmentReader.getMandatoryString(INDEX);

        DeleteRequest request = new DeleteRequest(index, TYPE, officerId);

        LoggingUtils.getLogger().info(String.format("Attempting to delete officer with given id: [%s] from primary search index", officerId), logMap);

        return request;
    }
}
