package uk.gov.companieshouse.search.api.service.upsert;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.ADVANCED_SEARCH_INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NUMBER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX_ALPHABETICAL;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;
import uk.gov.companieshouse.search.api.service.rest.impl.AdvancedSearchRestClientService;
import uk.gov.companieshouse.search.api.service.rest.impl.AlphabeticalSearchRestClientService;
import uk.gov.companieshouse.search.api.service.upsert.advanced.AdvancedUpsertRequestService;
import uk.gov.companieshouse.search.api.service.upsert.alphabetical.AlphabeticalUpsertRequestService;

@Service
public class UpsertCompanyService {

    @Autowired
    private AlphabeticalSearchRestClientService alphabeticalSearchRestClientService;

    @Autowired
    private AdvancedSearchRestClientService advancedSearchRestClientService;

    @Autowired
    private AlphabeticalUpsertRequestService alphabeticalUpsertRequestService;

    @Autowired
    private AdvancedUpsertRequestService advancedUpsertRequestService;

    @Autowired
    private AlphaKeyService alphaKeyService;

    /**
     * Upserts a new document to the alphabetical search index.
     * If a document does not exist it is added.
     * If the document does exist it is updated.
     *
     * @param company - Company sent over in REST call to be added/updated
     * @return {@link ResponseObject}
     */
    public ResponseObject upsert(CompanyProfileApi company) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(COMPANY_NAME, company.getCompanyName());
        logMap.put(COMPANY_NUMBER, company.getCompanyNumber());
        logMap.put(INDEX, INDEX_ALPHABETICAL);
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
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(COMPANY_NAME, company.getCompanyName());
        logMap.put(COMPANY_NUMBER, company.getCompanyNumber());
        logMap.put(INDEX, ADVANCED_SEARCH_INDEX);
        getLogger().info("Upserting to advanced index underway", logMap);

        IndexRequest indexRequest;
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
            indexRequest = advancedUpsertRequestService.createIndexRequest(company, orderedAlphaKey, sameAsKey);
            updateRequest = advancedUpsertRequestService.createUpdateRequest(company, orderedAlphaKey, sameAsKey, indexRequest);
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
}
