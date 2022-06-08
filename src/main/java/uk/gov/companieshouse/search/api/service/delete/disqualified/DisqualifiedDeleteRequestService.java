package uk.gov.companieshouse.search.api.service.delete.disqualified;

import org.elasticsearch.action.delete.DeleteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;

import java.util.Map;

@Service
public class DisqualifiedDeleteRequestService {

    private static final String INDEX = "DISQUALIFIED_SEARCH_INDEX";

    @Autowired
    private EnvironmentReader environmentReader;

    public DeleteRequest createDeleteRequest(String officerId) {

        Map<String, Object> logMap = LoggingUtils.setUpDisqualificationDeleteLogging(officerId);
        String index = environmentReader.getMandatoryString(INDEX);

        DeleteRequest request = new DeleteRequest(index, index, officerId);

        LoggingUtils.getLogger().info("Attempt to delete officer if it exists", logMap);

        return request;
    }

}
