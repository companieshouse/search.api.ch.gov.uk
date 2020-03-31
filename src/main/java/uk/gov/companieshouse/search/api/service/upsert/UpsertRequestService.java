package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.elasticsearch.AlphabeticalSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

import java.io.IOException;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class UpsertRequestService {

    @Autowired
    private AlphaKeyService alphaKeyService;

    @Autowired
    private EnvironmentReader environmentReader;

    @Autowired
    private AlphabeticalSearchUpsertRequest alphabeticalSearchUpsertRequest;

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private static final String INDEX = "ALPHABETICAL_SEARCH_INDEX";

    /**
     * Create an index request for document if it does not currently exist
     * @param company - Company sent over in REST call to be added/updated
     * @return {@link IndexRequest}
     * @throws UpsertException
     */
    public IndexRequest createIndexRequest(CompanyProfileApi company) throws UpsertException {

        String orderedAlphaKey = "";
        String orderedAlphaKeyWithID = "";

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(company.getCompanyName());
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
            orderedAlphaKeyWithID = alphaKeyResponse.getOrderedAlphaKey() + ":" + company.getCompanyNumber();
        }

        try {
            LOG.info("Preparing index request for "  + company.getCompanyName());
            return new IndexRequest(environmentReader.getMandatoryString(INDEX))
                .source(alphabeticalSearchUpsertRequest.buildRequest(company, orderedAlphaKey, orderedAlphaKeyWithID)).id(company.getCompanyNumber());
        } catch (IOException e) {
            LOG.error("Failed to index a document for company number: " + company.getCompanyName());
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

        String orderedAlphaKey = "";
        String orderedAlphaKeyWithID = "";

        AlphaKeyResponse alphaKeyResponse = alphaKeyService.getAlphaKeyForCorporateName(company.getCompanyName());
        if (alphaKeyResponse != null) {
            orderedAlphaKey = alphaKeyResponse.getOrderedAlphaKey();
            orderedAlphaKeyWithID = alphaKeyResponse.getOrderedAlphaKey() + ":" + company.getCompanyNumber();
        }

        try {
            LOG.info("Attempt to upsert document if it does not exist for "  + company.getCompanyName());

            return new UpdateRequest(environmentReader.getMandatoryString(INDEX), company.getCompanyNumber())
                .docAsUpsert(true)
                .doc(alphabeticalSearchUpsertRequest.buildRequest(company, orderedAlphaKey, orderedAlphaKeyWithID))
                .upsert(indexRequest);
        } catch (IOException e) {
            LOG.error("Failed to update a document for company: " + company.getCompanyName());
            throw new UpsertException("Unable to create update request");
        }
    }
}
