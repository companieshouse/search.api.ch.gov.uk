package uk.gov.companieshouse.search.api.service.upsert.officers;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.elasticsearch.OfficersSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument;

@Service
public class OfficersUpsertRequestService {

    private final OfficersSearchUpsertRequest officersSearchUpsertRequest;
    private final EnvironmentReader environmentReader;
    private static final String INDEX = "OFFICERS_SEARCH_INDEX";
    private static final String TYPE = "primary_search";
    private final ConversionService conversionService;

    public OfficersUpsertRequestService(OfficersSearchUpsertRequest officersSearchUpsertRequest,
            EnvironmentReader environmentReader, @Lazy ConversionService conversionService) {
        this.officersSearchUpsertRequest = officersSearchUpsertRequest;
        this.environmentReader = environmentReader;
        this.conversionService = conversionService;
    }

    /**
     * If document already exists attempt to upsert the document
     * Note: Uses a deprecated UpdateRequest method for ES6 index
     * @param appointmentList - Officers appointments sent over in REST call to be added/updated
     *
     * @return {@link UpdateRequest}
     */
    public UpdateRequest createUpdateRequest(AppointmentList appointmentList, String officerId)
            throws UpsertException {

        Map<String, Object> logMap = LoggingUtils.setUpOfficersAppointmentsUpsertLogging(appointmentList.getItems().get(0));
        String index = environmentReader.getMandatoryString(INDEX);

        if((appointmentList.getDateOfBirth() == null)
                || StringUtils.isBlank(appointmentList.getItems().get(0).getNameElements().getForename())
                || StringUtils.isBlank(appointmentList.getItems().get(0).getNameElements().getOtherForenames())) {
            appointmentList.setIsCorporateOfficer(true);
        }
        if(appointmentList.getItems().get(0).getNameElements().getSurname().contains(" ")){
            appointmentList.setIsCorporateOfficer(false);
        }

        OfficerSearchDocument documentToBeUpserted = Optional.ofNullable(conversionService.convert(appointmentList, OfficerSearchDocument.class))
                .orElseThrow();

        try {
            UpdateRequest request = new UpdateRequest(index, TYPE, officerId)
                    .docAsUpsert(true).doc(officersSearchUpsertRequest.buildRequest(documentToBeUpserted), XContentType.JSON);

            LoggingUtils.getLogger().info("Attempt to upsert document if it does not exist", logMap);

            return request;

        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for appointmentList" + e.getMessage(), logMap);
            throw new UpsertException("Unable to create update request");
        }
    }

}
