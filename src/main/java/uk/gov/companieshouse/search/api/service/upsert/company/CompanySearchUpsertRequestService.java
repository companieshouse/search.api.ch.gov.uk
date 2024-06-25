package uk.gov.companieshouse.search.api.service.upsert.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.esdatamodel.CompanySearchDocument;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

public class CompanySearchUpsertRequestService {

    private static final String TYPE = "primary_search";

    private final ConversionService companySearchDocumentConverter;

    private final ObjectMapper mapper;

    private final ConfiguredIndexNamesProvider indices;

    public CompanySearchUpsertRequestService(@Lazy ConversionService companySearchDocumentConverter,
            ObjectMapper mapper, ConfiguredIndexNamesProvider indices) {
        this.companySearchDocumentConverter = companySearchDocumentConverter;
        this.mapper = mapper;
        this.indices = indices;
    }

    public UpdateRequest createUpdateRequest(String companyNumber, Data profileData) throws UpsertException {

        Map<String, Object> logMap =
                LoggingUtils.setUpCompanySearchCompanyUpsertLogging(companyNumber, indices);

        CompanySearchDocument documentToBeUpserted = Optional.ofNullable(
                companySearchDocumentConverter.convert(profileData, CompanySearchDocument.class))
                .orElseThrow();

        try {
            String jsonString = mapper.writeValueAsString(documentToBeUpserted);

            return new UpdateRequest(indices.primary(), TYPE, companyNumber)
                    .docAsUpsert(true).doc(jsonString, XContentType.JSON);
        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for company profile" + e.getMessage(), logMap);
            throw new UpsertException("Unable to create update request");
        }
    }
}
