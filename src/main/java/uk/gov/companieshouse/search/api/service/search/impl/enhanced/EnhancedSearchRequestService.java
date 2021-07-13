package uk.gov.companieshouse.search.api.service.search.impl.enhanced;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;

@Service
public class EnhancedSearchRequestService {

    public SearchResults<Company> getSearchResults() {

        List<Company> results = new ArrayList<>();
        TopHit topHit = new TopHit();
        String etag = GenerateEtagUtil.generateEtag();
        String kind = "enhanced-search";
        Company dummyResult = new Company();
        Links links = new Links();
        LocalDate date = LocalDate.of(1999,04,11);

        Address address = new Address();
        address.setAddressLine1("ADDRESS_LINE_1");
        address.setAddressLine2("ADDRESS_LINE_2");
        address.setPostalCode("POSTCODE");
        address.setLocality("LOCALITY");

        links.setCompanyProfile("COMPANY_PROFILE_LINK");

        dummyResult.setCompanyName("Test company");
        dummyResult.setCompanyNumber("00000000");
        dummyResult.setCompanyStatus("active");
        dummyResult.setCompanyType("ltd");
        dummyResult.setOrderedAlphaKeyWithId("company00000000");
        dummyResult.setDateOfCessation(date);
        dummyResult.setDateOfCreation(date);
        dummyResult.setKind("kind");
        dummyResult.setRecordType("record type");
        dummyResult.setLinks(links);
        dummyResult.setRegisteredOfficeAddress(address);
        dummyResult.setPreviousCompanyNames(null);
        dummyResult.setMatchedPreviousCompanyName(null);

        results.add(dummyResult);

        return new SearchResults<>(etag, topHit, results, kind);
    }
}