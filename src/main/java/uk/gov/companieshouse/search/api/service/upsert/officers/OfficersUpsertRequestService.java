package uk.gov.companieshouse.search.api.service.upsert.officers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class OfficersUpsertRequestService {

    private static final String TYPE = "primary_search";
    private final ConversionService conversionService;
    private final ObjectMapper mapper;
    private final ConfiguredIndexNamesProvider indices;

    public OfficersUpsertRequestService(@Lazy ConversionService conversionService, ObjectMapper mapper,
        ConfiguredIndexNamesProvider indices) {
        this.conversionService = conversionService;
        this.mapper = mapper;
        this.indices = indices;
    }

    public UpdateRequest createUpdateRequest(AppointmentList appointmentList, String officerId)
            throws UpsertException {

        Map<String, Object> logMap =
            LoggingUtils.setUpOfficersAppointmentsUpsertLogging(officerId, indices);

        OfficerSearchDocument documentToBeUpserted = Optional.ofNullable(conversionService.convert(appointmentList, OfficerSearchDocument.class))
                .orElseThrow();

        try {
            String jsonString = mapper.writeValueAsString(documentToBeUpserted);

            return new UpdateRequest(indices.primary(), TYPE, officerId)
                    .docAsUpsert(true).doc(jsonString, XContentType.JSON);

        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for appointmentList" + e.getMessage(), logMap);
            throw new UpsertException("Unable to create update request");
        }
    }

}
