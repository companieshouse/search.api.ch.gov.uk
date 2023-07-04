package uk.gov.companieshouse.search.api.service.upsert.officers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument;

@Service
public class OfficersUpsertRequestService {

    private final EnvironmentReader environmentReader;
    private static final String INDEX = "PRIMARY_SEARCH_INDEX";
    private static final String TYPE = "primary_search";
    private final ConversionService conversionService;

    public OfficersUpsertRequestService(EnvironmentReader environmentReader, @Lazy ConversionService conversionService) {
        this.environmentReader = environmentReader;
        this.conversionService = conversionService;
    }

    public UpdateRequest createUpdateRequest(AppointmentList appointmentList, String officerId)
            throws UpsertException {

        Map<String, Object> logMap = LoggingUtils.setUpOfficersAppointmentsUpsertLogging(officerId);
        String index = environmentReader.getMandatoryString(INDEX);

        OfficerSearchDocument documentToBeUpserted = Optional.ofNullable(conversionService.convert(appointmentList, OfficerSearchDocument.class))
                .orElseThrow();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String jsonString = mapper.writeValueAsString(documentToBeUpserted);

            UpdateRequest request = new UpdateRequest(index, TYPE, officerId)
                    .docAsUpsert(true).doc(jsonString, XContentType.JSON);

            LoggingUtils.getLogger().info("Attempt to upsert document if it does not exist", logMap);

            return request;

        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for appointmentList" + e.getMessage(), logMap);
            throw new UpsertException("Unable to create update request");
        }
    }

}
