package uk.gov.companieshouse.search.api.service.upsert.psc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.psc.PscSummary;
import uk.gov.companieshouse.search.api.model.psc.PscSearchDocument;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class PscUpsertRequestService {

    private static final String TYPE = "primary_search";
    private final ConversionService conversionService;
    private final ObjectMapper mapper;
    private final ConfiguredIndexNamesProvider indices;

    public PscUpsertRequestService(@Lazy ConversionService conversionService, ObjectMapper mapper,
                                   ConfiguredIndexNamesProvider indices) {
        this.conversionService = conversionService;
        this.mapper = mapper;
        this.indices = indices;
    }

    public UpdateRequest createUpdateRequest(PscSummary pscSummary, String pscId) throws UpsertException {
        LoggingUtils.getLogger().info("Creating update request for PSC: " + pscId);
        PscSearchDocument documentToBeUpserted = Optional.ofNullable(conversionService.convert(pscSummary, PscSearchDocument.class))
                .orElseThrow();
        try {
            String jsonString = mapper.writeValueAsString(documentToBeUpserted);
            return new UpdateRequest(indices.primary(), TYPE, pscId)
                    .docAsUpsert(true).doc(jsonString, XContentType.JSON);
        } catch (IOException ex) {
            LoggingUtils.getLogger().error(String.format("Error: failed to create update request for PSC: %s.", pscId), ex);
            throw new UpsertException(String.format("Error: failed to create update request for PSC: %s.", pscId));
        }
    }
}
