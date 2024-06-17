package uk.gov.companieshouse.search.api.service.upsert;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.PrimarySearchRestClientService;
import uk.gov.companieshouse.search.api.service.upsert.advanced.AdvancedUpsertRequestService;
import uk.gov.companieshouse.search.api.service.upsert.alphabetical.AlphabeticalUpsertRequestService;
import uk.gov.companieshouse.search.api.service.upsert.company.CompanySearchUpsertRequestService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.*;

@Service
public class UpsertCompanyService {

    private final AlphabeticalSearchRestClientService alphabeticalSearchRestClientService;
    private final AdvancedSearchRestClientService advancedSearchRestClientService;
    private final AlphabeticalUpsertRequestService alphabeticalUpsertRequestService;
    private final AdvancedUpsertRequestService advancedUpsertRequestService;

    private final PrimarySearchRestClientService primarySearchRestClientService;
    private final CompanySearchUpsertRequestService companySearchUpsertRequestService;
    private final AlphaKeyService alphaKeyService;
    private final ConfiguredIndexNamesProvider indices;

    public UpsertCompanyService(
        AlphabeticalSearchRestClientService alphabeticalSearchRestClientService,
        AdvancedSearchRestClientService advancedSearchRestClientService,
        AlphabeticalUpsertRequestService alphabeticalUpsertRequestService,
        AdvancedUpsertRequestService advancedUpsertRequestService,
        PrimarySearchRestClientService primarySearchRestClientService,
        CompanySearchUpsertRequestService companySearchUpsertRequestService, AlphaKeyService alphaKeyService,
        ConfiguredIndexNamesProvider indices) {
        this.alphabeticalSearchRestClientService = alphabeticalSearchRestClientService;
        this.advancedSearchRestClientService = advancedSearchRestClientService;
        this.alphabeticalUpsertRequestService = alphabeticalUpsertRequestService;
        this.advancedUpsertRequestService = advancedUpsertRequestService;
        this.primarySearchRestClientService = primarySearchRestClientService;
        this.companySearchUpsertRequestService = companySearchUpsertRequestService;
        this.alphaKeyService = alphaKeyService;
        this.indices = indices;
    }

    /**
     * Upserts a new document to the alphabetical search index.
     * If a document does not exist it is added.
     * If the document does exist it is updated.
     *
     * @param company - Company sent over in REST call to be added/updated
     * @return {@link ResponseObject}
     */
    public ResponseObject upsert(CompanyProfileApi company) {
        Map<String, Object> logMap = new DataMap.Builder()
                .companyName(company.getCompanyName())
                .companyNumber(company.getCompanyNumber())
                .indexName(indices.alphabetical())
                .build().getLogMap();
        getLogger().info("Upserting company underway", logMap);

        IndexRequest indexRequest;
        UpdateRequest updateRequest;

        try {
            indexRequest = alphabeticalUpsertRequestService.createIndexRequest(company);
            updateRequest = alphabeticalUpsertRequestService.createUpdateRequest(company, indexRequest);
        } catch (UpsertException e) {
            getLogger().error("An error occured attempting upsert the document", logMap);
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            alphabeticalSearchRestClientService.upsert(updateRequest);
        } catch (IOException e) {
            getLogger().error("IOException when upserting company", logMap);
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        getLogger().info("Upsert successful for ", logMap);
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }

    /**
     * Upserts a new document to advanced search index.
     * If a document does not exist it is added.
     * If the document does exist it is updated.
     *
     * @param company - Company sent over in REST call to be added/updated
     * @return {@link ResponseObject}
     */
    public ResponseObject upsertAdvanced(CompanyProfileApi company) {
        Map<String, Object> logMap = new DataMap.Builder()
                .companyName(company.getCompanyName())
                .companyNumber(company.getCompanyNumber())
                .indexName(indices.advanced())
                .build().getLogMap();
        getLogger().info("Upserting to advanced index underway", logMap);

        UpdateRequest updateRequest;

        String orderedAlphaKey = "";
        String sameAsKey = "";

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(company.getCompanyName());
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
            sameAsKey = alphaKeyResponse.getSameAsAlphaKey();
            logMap.put(LoggingUtils.ORDERED_ALPHAKEY, orderedAlphaKey);
            logMap.put(LoggingUtils.SAME_AS_ALPHAKEYKEY, sameAsKey);
        }

        try {
            updateRequest = advancedUpsertRequestService.createUpdateRequest(company, orderedAlphaKey, sameAsKey);
        } catch (UpsertException e) {
            getLogger().error("An error occured attempting upsert the document to advanced search index", logMap);
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            advancedSearchRestClientService.upsert(updateRequest);
        } catch (IOException e) {
            getLogger().error("IOException when upserting a company to the advanced search index", logMap);
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        getLogger().info("Upsert successful to advanced search index", logMap);
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }

    public ResponseObject upsertCompany(String companyNumber, Data profileData) {
        Map<String, Object> logMap =
                LoggingUtils.setUpCompanySearchCompanyUpsertLogging(companyNumber, indices);
        getLogger().info("Upserting company profile to primary index", logMap);

        UpdateRequest updateRequest;
        try {
            updateRequest = companySearchUpsertRequestService.createUpdateRequest(companyNumber, profileData);
        } catch (UpsertException e) {
            getLogger().error("An error occurred attempting upsert the document to primary search "
                    + "index", logMap);
            return new ResponseObject(ResponseStatus.UPSERT_ERROR);
        }

        try {
            primarySearchRestClientService.upsert(updateRequest);
        } catch (IOException e) {
            getLogger().error("IOException when upserting an company profile to primary search "
                    + "index", logMap);
            return new ResponseObject(ResponseStatus.SERVICE_UNAVAILABLE);
        } catch (ElasticsearchException e) {
            return new ResponseObject(ResponseStatus.UPDATE_REQUEST_ERROR);
        }

        getLogger().info("Company profile Upsert successful to primary search index", logMap);
        return new ResponseObject(ResponseStatus.DOCUMENT_UPSERTED);
    }
}
