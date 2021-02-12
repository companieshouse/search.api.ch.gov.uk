package uk.gov.companieshouse.search.api.service.search.impl.dissolved;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.companieshouse.search.api.SearchApiApplication.APPLICATION_NAME_SPACE;

@Service
public class DissolvedSearchIndexService {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private static final String DISSOLVED_SEARCH = "Dissolved search: ";

    public DissolvedResponseObject search(String companyName, String requestId) {

        TopHit topHit = new TopHit();
        topHit.setCompanyName("TEST COMPANY NAME");
        topHit.setCompanyNumber("TEST COMPANY NUMBER");

        List<DissolvedCompany> dissolvedCompanies = new ArrayList<>();
        DissolvedCompany dissolvedCompany = new DissolvedCompany();
        Address address = new Address();
        address.setLocality("TEST LOCALITY");
        address.setPostalCode("AB12 3CD");

        PreviousCompanyName previousCompanyName = new PreviousCompanyName();
        previousCompanyName.setDateOfNameCessation("01/01/1993");
        previousCompanyName.setDateOfNameEffectiveness("01/01/1983");
        previousCompanyName.setName("PREVIOUS NAME");

        List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
        previousCompanyNames.add(previousCompanyName);

        dissolvedCompany.setCompanyName("TEST COMPANY NAME");
        dissolvedCompany.setCompanyNumber("TEST COMPANY NUMBER");
        dissolvedCompany.setCompanyStatus("TEST COMPANY STATUS");
        dissolvedCompany.setDateOfCessation("TEST DATE OF CESSATION");
        dissolvedCompany.setDateOfCreation("TEST DATE OF CREATION");
        dissolvedCompany.setAddress(address);
        dissolvedCompany.setPreviousCompanyNames(previousCompanyNames);

        dissolvedCompanies.add(dissolvedCompany);

        DissolvedSearchResults searchResults = new DissolvedSearchResults();
        searchResults.setTopHit(topHit);
        searchResults.setEtag("TEST");
        searchResults.setItems(dissolvedCompanies);

        LOG.info(DISSOLVED_SEARCH + "successful for: " + companyName);
        return new DissolvedResponseObject(ResponseStatus.SEARCH_FOUND, searchResults);
    }
}
