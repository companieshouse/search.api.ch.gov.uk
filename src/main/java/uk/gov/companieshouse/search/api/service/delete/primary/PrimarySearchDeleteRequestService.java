package uk.gov.companieshouse.search.api.service.delete.primary;

import org.elasticsearch.action.delete.DeleteRequest;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.SearchType;

import java.util.Map;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class PrimarySearchDeleteRequestService {

    private static final String TYPE = "primary_search";

    private final ConfiguredIndexNamesProvider indices;

    public PrimarySearchDeleteRequestService(ConfiguredIndexNamesProvider indices) {
        this.indices = indices;
    }

    public DeleteRequest createDeleteRequest(SearchType searchType) {

        Map<String, Object> logMap =
            LoggingUtils.setUpPrimarySearchDeleteLogging(searchType.getOfficerId(), indices);

        DeleteRequest request = new DeleteRequest(indices.primary(), TYPE, searchType.getOfficerId());

        LoggingUtils.getLogger().info(String.format("Attempting to delete an [%s] with given id: [%s] from primary search index",
                searchType.getPrimarySearchType(), searchType.getOfficerId()), logMap);

        return request;
    }
}
