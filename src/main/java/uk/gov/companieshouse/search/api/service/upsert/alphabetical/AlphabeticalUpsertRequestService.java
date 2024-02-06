package uk.gov.companieshouse.search.api.service.upsert.alphabetical;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@Service
public class AlphabeticalUpsertRequestService {

    private final AlphaKeyService alphaKeyService;

    private final AlphabeticalSearchUpsertRequest alphabeticalSearchUpsertRequest;

    private final ConfiguredIndexNamesProvider indices;

    public AlphabeticalUpsertRequestService(AlphaKeyService alphaKeyService,
        AlphabeticalSearchUpsertRequest alphabeticalSearchUpsertRequest,
        ConfiguredIndexNamesProvider indices) {
        this.alphaKeyService = alphaKeyService;
        this.alphabeticalSearchUpsertRequest = alphabeticalSearchUpsertRequest;
        this.indices = indices;
    }

    /**
     * Create an index request for document if it does not currently exist
     * @param company - Company sent over in REST call to be added/updated
     * @return {@link IndexRequest}
     * @throws UpsertException
     */
    public IndexRequest createIndexRequest(CompanyProfileApi company) throws UpsertException {

        Map<String, Object> logMap = new DataMap.Builder()
                .companyName(company.getCompanyName())
                .companyNumber(company.getCompanyNumber())
                .indexName(indices.alphabetical())
                .build().getLogMap();

        String orderedAlphaKey = "";
        String orderedAlphaKeyWithID = "";

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(company.getCompanyName());
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
            orderedAlphaKeyWithID = alphaKeyResponse.getOrderedAlphaKey() + ":" + company.getCompanyNumber();
            logMap.put(LoggingUtils.ORDERED_ALPHAKEY, orderedAlphaKey);
        }

        try {
            LoggingUtils.getLogger().info("Preparing index request", logMap);
            return new IndexRequest(indices.alphabetical())
                .source(alphabeticalSearchUpsertRequest.buildRequest(company, orderedAlphaKey, orderedAlphaKeyWithID)).id(company.getCompanyNumber());
        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to index a document for company", logMap);
            throw new UpsertException("Unable create index request");
        }
    }

    /**
     * If document already exists attempt to upsert the document
     * @param company - Company sent over in REST call to be added/updated
     * @param indexRequest
     * @return {@link UpdateRequest}
     * @throws UpsertException
     */
    public UpdateRequest createUpdateRequest(CompanyProfileApi company, IndexRequest indexRequest)
        throws UpsertException {
        Map<String, Object> logMap = new DataMap.Builder()
                .companyName(company.getCompanyName())
                .companyNumber(company.getCompanyNumber())
                .indexName(indices.alphabetical())
                .build().getLogMap();

        String orderedAlphaKey = "";
        String orderedAlphaKeyWithID = "";

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(company.getCompanyName());
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
            logMap.put(LoggingUtils.ORDERED_ALPHAKEY, orderedAlphaKey);
            orderedAlphaKeyWithID = alphaKeyResponse.getOrderedAlphaKey() + ":" + company.getCompanyNumber();
        }

        try {
            LoggingUtils.getLogger().info("Attempt to upsert document if it does not exist", logMap);

            return new UpdateRequest(indices.alphabetical(), company.getCompanyNumber())
                .docAsUpsert(true)
                .doc(alphabeticalSearchUpsertRequest.buildRequest(company, orderedAlphaKey, orderedAlphaKeyWithID))
                .upsert(indexRequest);
        } catch (IOException e) {
            LoggingUtils.getLogger().error("Failed to update a document for company", logMap);
            throw new UpsertException("Unable to create update request");
        }
    }
}
