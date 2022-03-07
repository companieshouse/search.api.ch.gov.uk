package uk.gov.companieshouse.search.api.mapper;

import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.search.api.constants.TestConstants.ALPHABETICAL_RESPONSE;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_INNER_HITS;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_RESPONSE;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_RESPONSE_NO_ADDRESS_LINES;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_RESPONSE_NO_DATES;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_RESPONSE_NO_LOCALITY;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_RESPONSE_NO_POSTCODE;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_RESPONSE_NO_PREVIOUS_NAME;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_RESPONSE_NO_ROA;
import static uk.gov.companieshouse.search.api.constants.TestConstants.DISSOLVED_RESPONSE_POST_20_YEARS;
import static uk.gov.companieshouse.search.api.constants.TestConstants.ADVANCED_RESPONSE;
import static uk.gov.companieshouse.search.api.constants.TestConstants.ADVANCED_RESPONSE_DISSOLVED_COMPANY;
import static uk.gov.companieshouse.search.api.constants.TestConstants.ADVANCED_RESPONSE_MISSING_FIELDS;
import static uk.gov.companieshouse.search.api.constants.TestConstants.ADVANCED_RESPONSE_WITH_DISSOLVED_DATE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.search.api.model.TopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.Company;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.model.esdatamodel.PreviousCompanyName;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ElasticSearchResponseMapperTest {

    @InjectMocks
    private ElasticSearchResponseMapper elasticSearchResponseMapper;

    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String COMPANY_STATUS = "dissolved";
    private static final String COMPANY_STATUS_ACTIVE = "active";
    private static final String COMPANY_TYPE = "ltd";
    private static final String COMPANY_PROFILE_LINK = "/company/00000000";
    private static final String KIND = "searchresults#dissolved-company";
    private static final String COMPANY_KIND = "search-results#company";
    private static final String DATE_OF_CESSATION = "2010-05-01";
    private static final String DATE_OF_CESSATION_POST_20_YEARS = "1991-05-01";
    private static final String DATE_OF_CREATION = "1989-05-01";
    private static final String LOCALITY = "locality";
    private static final String POSTCODE = "AB00 0 AB";
    private static final String PREVIOUS_COMPANY_NAME = "TEST COMPANY 2";
    private static final String ADDRESS_LINE_1 = "TEST STREET";
    private static final String ADDRESS_LINE_2 = "TEST TOWN";
    private static final String EFFECTIVE_FROM = "1989-01-01";
    private static final String CEASED_ON = "1992-05-10";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String SIC_CODES = "99960";
    private static final List<String> SIC_CODES_LIST = Arrays.asList(SIC_CODES);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    @Test
    @DisplayName("Map alphabetical response successful")
    void mapAlphabeticalResponseTest() {

        SearchHits searchHits = createHits(ALPHABETICAL_RESPONSE);

        Company company =
                elasticSearchResponseMapper.mapAlphabeticalResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, company.getCompanyName());
        assertEquals(COMPANY_NUMBER, company.getCompanyNumber());
        assertEquals(COMPANY_STATUS_ACTIVE, company.getCompanyStatus());
        assertEquals(COMPANY_TYPE, company.getCompanyType());
        assertEquals(ORDERED_ALPHA_KEY_WITH_ID, company.getOrderedAlphaKeyWithId());
        assertEquals(COMPANY_PROFILE_LINK, company.getLinks().getCompanyProfile());
    }

    @Test
    @DisplayName("Map dissolved response successful")
    void mapDissolvedResponseTest() {

        SearchHits searchHits = createHits(DISSOLVED_RESPONSE);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, company.getCompanyName());
        assertEquals(COMPANY_NUMBER, company.getCompanyNumber());
        assertEquals(COMPANY_STATUS, company.getCompanyStatus());
        assertEquals(KIND, company.getKind());
        assertEquals(DATE_OF_CESSATION, company.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, company.getDateOfCreation().toString());
        assertEquals(LOCALITY, company.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, company.getRegisteredOfficeAddress().getPostalCode());

        List<PreviousCompanyName> previousCompanyNames = company.getPreviousCompanyNames();
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());
    }

    @Test
    @DisplayName("Map dissolved response successful date of cessation greater than 20 years old")
    void mapDissolvedResponseCessationGreaterThan20Test() {

        SearchHits searchHits = createHits(DISSOLVED_RESPONSE_POST_20_YEARS);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, company.getCompanyName());
        assertEquals(COMPANY_NUMBER, company.getCompanyNumber());
        assertEquals(COMPANY_STATUS, company.getCompanyStatus());
        assertEquals(KIND, company.getKind());
        assertEquals(DATE_OF_CESSATION_POST_20_YEARS, company.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, company.getDateOfCreation().toString());

        assertNull(company.getRegisteredOfficeAddress());

        List<PreviousCompanyName> previousCompanyNames = company.getPreviousCompanyNames();
        assertPreviousCompanyName(previousCompanyNames);
    }

    @Test
    @DisplayName("Map dissolved response no dates present successful")
    void mapDissolvedResponseNoDatesPresentTest() {

        SearchHits searchHits = createHits(DISSOLVED_RESPONSE_NO_DATES);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, company.getCompanyName());
        assertEquals(COMPANY_NUMBER, company.getCompanyNumber());
        assertEquals(COMPANY_STATUS, company.getCompanyStatus());
        assertEquals(KIND, company.getKind());
        assertNull(company.getDateOfCessation());
        assertNull(company.getDateOfCreation());

        List<PreviousCompanyName> previousCompanyNames = company.getPreviousCompanyNames();
        assertPreviousCompanyName(previousCompanyNames);
    }

    @Test
    @DisplayName("Map dissolved response successful when no previous names")
    void mapDissolvedResponseTestPreviousNamesNull() {

        SearchHits searchHits = createHits(DISSOLVED_RESPONSE_NO_PREVIOUS_NAME);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        List<PreviousCompanyName> previousCompanyNames = company.getPreviousCompanyNames();
        assertNull(previousCompanyNames);
    }

    @Test
    @DisplayName("Map dissolved response successful when no address lines")
    void mapDissolvedResponseTestNoAddressLines() {

        SearchHits searchHits = createHits(DISSOLVED_RESPONSE_NO_ADDRESS_LINES);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = company.getRegisteredOfficeAddress();
        assertEquals(POSTCODE, address.getPostalCode());
        assertEquals(LOCALITY, address.getLocality());
        assertNull(address.getAddressLine1());
        assertNull(address.getAddressLine2());
    }

    @Test
    @DisplayName("Map dissolved response successful when no locality")
    void mapDissolvedResponseTestNoLocality() {

        SearchHits searchHits = createHits(DISSOLVED_RESPONSE_NO_LOCALITY);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = company.getRegisteredOfficeAddress();
        assertEquals(POSTCODE, address.getPostalCode());
        assertNull(address.getLocality());
    }

    @Test
    @DisplayName("Map dissolved response successful when no postcode")
    void mapDissolvedResponseTestNoPostcode() {

        SearchHits searchHits = createHits(DISSOLVED_RESPONSE_NO_POSTCODE);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = company.getRegisteredOfficeAddress();
        assertEquals(LOCALITY, address.getLocality());
        assertNull(address.getPostalCode());
    }

    @Test
    @DisplayName("Map dissolved response successful when no address")
    void mapDissolvedResponseTestNoAddress() {

        SearchHits searchHits = createHits(DISSOLVED_RESPONSE_NO_ROA);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = company.getRegisteredOfficeAddress();
        assertNull(address);
    }

    @Test
    @DisplayName("Map alphabetical top hit successful")
    void mapAlphabeticalTopHitSuccessful() {

        TopHit topHit = elasticSearchResponseMapper.mapAlphabeticalTopHit(createAlphabeticalCompany());

        assertEquals(COMPANY_NAME, topHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, topHit.getCompanyNumber());
        assertEquals(COMPANY_STATUS, topHit.getCompanyStatus());
        assertEquals(COMPANY_TYPE, topHit.getCompanyType());
        assertEquals(ORDERED_ALPHA_KEY_WITH_ID, topHit.getOrderedAlphaKeyWithId());
        assertEquals(COMPANY_PROFILE_LINK, topHit.getLinks().getCompanyProfile());
    }

    @Test
    @DisplayName("Map advanced top hit successful")
    void mapEmhancedTopHitSuccessful() {

        TopHit topHit = elasticSearchResponseMapper.mapAdvancedTopHit(createAdvancedCompany());

        assertEquals(COMPANY_NAME, topHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, topHit.getCompanyNumber());
        assertEquals(COMPANY_STATUS, topHit.getCompanyStatus());
        assertEquals(COMPANY_TYPE, topHit.getCompanyType());
        assertEquals(KIND, topHit.getKind());
        assertEquals(DATE_OF_CESSATION, topHit.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, topHit.getDateOfCreation().toString());
        assertEquals(SIC_CODES_LIST, topHit.getSicCodes());
        assertEquals(COMPANY_PROFILE_LINK, topHit.getLinks().getCompanyProfile());
    }

    @Test
    @DisplayName("Map dissolved top hit successful")
    void mapTopHitSuccessful() {

        TopHit topHit = elasticSearchResponseMapper.mapDissolvedTopHit(createCompany(true, true, true));

        assertEquals(COMPANY_NAME, topHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, topHit.getCompanyNumber());
        assertEquals(COMPANY_STATUS, topHit.getCompanyStatus());
        assertEquals(KIND, topHit.getKind());
        assertEquals(DATE_OF_CESSATION, topHit.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, topHit.getDateOfCreation().toString());
        assertEquals(LOCALITY, topHit.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, topHit.getRegisteredOfficeAddress().getPostalCode());

        List<PreviousCompanyName> previousCompanyNames = topHit.getPreviousCompanyNames();
        assertPreviousCompanyName(previousCompanyNames);

        PreviousCompanyName matchedPreviousCompanyNames = topHit.getMatchedPreviousCompanyName();
        assertEquals(PREVIOUS_COMPANY_NAME, matchedPreviousCompanyNames.getName());
        assertEquals(EFFECTIVE_FROM, matchedPreviousCompanyNames.getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, matchedPreviousCompanyNames.getDateOfNameCessation().toString());
        assertEquals(COMPANY_NUMBER, matchedPreviousCompanyNames.getCompanyNumber());
    }

    @Test
    @DisplayName("Map dissolved top hit successful no previous names")
    void mapTopHitSuccessfulNoPreviousNames() {

        TopHit topHit = elasticSearchResponseMapper.mapDissolvedTopHit(createCompany(false, true, false));

        assertEquals(COMPANY_NAME, topHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, topHit.getCompanyNumber());
        assertEquals(COMPANY_STATUS, topHit.getCompanyStatus());
        assertEquals(KIND, topHit.getKind());
        assertEquals(DATE_OF_CESSATION, topHit.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, topHit.getDateOfCreation().toString());
        assertEquals(LOCALITY, topHit.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, topHit.getRegisteredOfficeAddress().getPostalCode());

        assertNull(topHit.getPreviousCompanyNames());
    }

    @Test
    @DisplayName("Map dissolved top hit successful no matching previous name")
    void mapTopHitNoMatchingPreviousName() {

        TopHit previousNamesTopHit = elasticSearchResponseMapper.mapDissolvedTopHit(createCompany(true, true, false));

        assertEquals(COMPANY_NAME, previousNamesTopHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousNamesTopHit.getCompanyNumber());
        assertEquals(COMPANY_STATUS, previousNamesTopHit.getCompanyStatus());
        assertEquals(KIND, previousNamesTopHit.getKind());
        assertEquals(DATE_OF_CESSATION, previousNamesTopHit.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousNamesTopHit.getDateOfCreation().toString());
        assertEquals(LOCALITY, previousNamesTopHit.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, previousNamesTopHit.getRegisteredOfficeAddress().getPostalCode());

        List<PreviousCompanyName> previousCompanyNames = previousNamesTopHit.getPreviousCompanyNames();
        assertPreviousCompanyName(previousCompanyNames);

        assertNull(previousNamesTopHit.getMatchedPreviousCompanyName());
    }

    @Test
    @DisplayName("Map dissolved top hit successful no address")
    void mapTopHitNoAddress() {

        TopHit previousNamesTopHit = elasticSearchResponseMapper.mapDissolvedTopHit(createCompany(false, false, false));

        assertEquals(COMPANY_NAME, previousNamesTopHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousNamesTopHit.getCompanyNumber());
        assertEquals(KIND, previousNamesTopHit.getKind());
        assertEquals(DATE_OF_CESSATION, previousNamesTopHit.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousNamesTopHit.getDateOfCreation().toString());
        assertNull(previousNamesTopHit.getRegisteredOfficeAddress());
    }

    @Test
    @DisplayName("Map dissolved previous names successful")
    void mapDissolvedPreviousNames() {

        SearchHits searchHits = createSearchHitsWithInnerHits(DISSOLVED_RESPONSE);

        List<Company> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        Company previousName = previousNames.get(0);
        assertEquals(COMPANY_NAME, previousName.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousName.getCompanyNumber());
        assertEquals(COMPANY_STATUS, previousName.getCompanyStatus());
        assertEquals(KIND, previousName.getKind());
        assertEquals(DATE_OF_CESSATION, previousName.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousName.getDateOfCreation().toString());
        assertEquals(POSTCODE, previousName.getRegisteredOfficeAddress().getPostalCode());
    }

    @Test
    @DisplayName("Map dissolved previous names successful no address object")
    void mapDissolvedPreviousNamesNoAddress() {

        SearchHits searchHits = createSearchHitsWithInnerHits(DISSOLVED_RESPONSE_NO_ROA);

        List<Company> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        Company previousName = previousNames.get(0);
        assertEquals(COMPANY_NAME, previousName.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousName.getCompanyNumber());
        assertEquals(KIND, previousName.getKind());
        assertEquals(DATE_OF_CESSATION, previousName.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousName.getDateOfCreation().toString());
    }

    @Test
    @DisplayName("Map dissolved previous names successful no postal code")
    void mapDissolvedPreviousNamesNoPostalCode() {

        SearchHits searchHits = createSearchHitsWithInnerHits(DISSOLVED_RESPONSE_NO_POSTCODE);

        List<Company> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        Company previousName = previousNames.get(0);
        assertEquals(COMPANY_NAME, previousName.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousName.getCompanyNumber());
        assertEquals(KIND, previousName.getKind());
        assertEquals(DATE_OF_CESSATION, previousName.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousName.getDateOfCreation().toString());
        assertNull(previousName.getRegisteredOfficeAddress().getPostalCode());
    }

    @Test
    @DisplayName("Map dissolved previous names successful no previous names")
    void mapDissolvedPreviousNamesNoPreviousNames() {

        SearchHits searchHits = createSearchHitsWithInnerHits(DISSOLVED_RESPONSE_NO_PREVIOUS_NAME);

        List<Company> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        Company previousName = previousNames.get(0);
        assertEquals(COMPANY_NAME, previousName.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousName.getCompanyNumber());
        assertEquals(KIND, previousName.getKind());
        assertEquals(DATE_OF_CESSATION, previousName.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousName.getDateOfCreation().toString());
        assertNull(previousName.getPreviousCompanyNames());
    }

    @Test
    @DisplayName("Map advanced response successful for active company")
    void mapAdvancedResponseSuccessfulActiveTest() {

        SearchHits searchHits = createHits(ADVANCED_RESPONSE);

        Company company =
                elasticSearchResponseMapper.mapAdvancedSearchResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, company.getCompanyName());
        assertEquals(COMPANY_NUMBER, company.getCompanyNumber());
        assertEquals(COMPANY_STATUS_ACTIVE, company.getCompanyStatus());
        assertEquals(COMPANY_KIND, company.getKind());
        assertEquals(DATE_OF_CREATION, company.getDateOfCreation().toString());
        assertEquals(LOCALITY, company.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, company.getRegisteredOfficeAddress().getPostalCode());
        assertEquals(SIC_CODES_LIST, company.getSicCodes());
    }
    
    @Test
    @DisplayName("Map advanced response for an active company with a dissoled date does not return the dissolved date")
    void mapAdvancedResponseActiveCompanyWithDissolvedDate() {

        SearchHits searchHits = createHits(ADVANCED_RESPONSE_WITH_DISSOLVED_DATE);

        Company company =
                elasticSearchResponseMapper.mapAdvancedSearchResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, company.getCompanyName());
        assertEquals(COMPANY_NUMBER, company.getCompanyNumber());
        assertEquals(COMPANY_STATUS_ACTIVE, company.getCompanyStatus());
        assertEquals(COMPANY_KIND, company.getKind());
        assertEquals(DATE_OF_CREATION, company.getDateOfCreation().toString());
        assertEquals(LOCALITY, company.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, company.getRegisteredOfficeAddress().getPostalCode());
        assertEquals(SIC_CODES_LIST, company.getSicCodes());
        assertNull(company.getDateOfCessation());
    }

    @Test
    @DisplayName("Map advanced response successful with missing fields and null date of creation and cessation")
    void mapAdvancedResponseSuccessfulMissingFields() {

        SearchHits searchHits = createHits(ADVANCED_RESPONSE_MISSING_FIELDS);

        Company company =
                elasticSearchResponseMapper.mapAdvancedSearchResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, company.getCompanyName());
        assertEquals(COMPANY_NUMBER, company.getCompanyNumber());
        assertEquals(COMPANY_STATUS, company.getCompanyStatus());
        assertEquals(COMPANY_KIND, company.getKind());
        assertEquals(null, company.getDateOfCreation());
        assertEquals(null, company.getDateOfCessation());
    }

    @Test
    @DisplayName("Map advanced response successful for dissolved company")
    void mapAdvancedResponseSuccessfulDissolvedTest() {

        SearchHits searchHits = createHits(ADVANCED_RESPONSE_DISSOLVED_COMPANY);

        Company company =
                elasticSearchResponseMapper.mapAdvancedSearchResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, company.getCompanyName());
        assertEquals(COMPANY_NUMBER, company.getCompanyNumber());
        assertEquals(COMPANY_STATUS, company.getCompanyStatus());
        assertEquals(COMPANY_KIND, company.getKind());
        assertEquals(DATE_OF_CREATION, company.getDateOfCreation().toString());
        assertEquals(LOCALITY, company.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, company.getRegisteredOfficeAddress().getPostalCode());
        assertEquals(SIC_CODES_LIST, company.getSicCodes());
    }

    private SearchHits createHits(String json) {

        BytesReference source = new BytesArray(json);
        SearchHit hit = new SearchHit(1);
        hit.sourceRef(source);

        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits(new SearchHit[]{hit}, totalHits, 10);
    }

    private void assertPreviousCompanyName(List<PreviousCompanyName> previousCompanyNames) {
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());
        assertEquals(COMPANY_NUMBER, previousCompanyNames.get(0).getCompanyNumber());
    }

    private SearchHits createSearchHitsWithInnerHits(String json) {

        SearchHits searchHits = createHits(json);
        SearchHit searchHit = searchHits.getAt(0);

        Map<String, SearchHits> innerHits = new HashMap<>();

        BytesReference source = new BytesArray(DISSOLVED_INNER_HITS);
        SearchHit innerHit = new SearchHit(100);
        innerHit.sourceRef(source);

        innerHits.put("previous_company_names", new SearchHits(new SearchHit[]{innerHit}, new TotalHits(1, TotalHits.Relation.EQUAL_TO), 1f));

        searchHit.setInnerHits(innerHits);

        return searchHits;

    }

    private Company createCompany(boolean includePreviousName, boolean includeAddress, boolean includeMatchedPreviousName) {
        Company company = new Company();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setKind(KIND);
        company.setDateOfCessation(LocalDate.parse("20100501", formatter));
        company.setDateOfCreation(LocalDate.parse("19890501", formatter));
        company.setSicCodes(SIC_CODES_LIST);

        if (includeAddress) {
            Address address = new Address();
            address.setAddressLine1(ADDRESS_LINE_1);
            address.setAddressLine2(ADDRESS_LINE_2);
            address.setPostalCode(POSTCODE);
            address.setLocality(LOCALITY);
            company.setRegisteredOfficeAddress(address);
        }

        if (includePreviousName) {
            List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
            PreviousCompanyName previousCompanyName = new PreviousCompanyName();
            previousCompanyName.setName(PREVIOUS_COMPANY_NAME);
            previousCompanyName.setDateOfNameCessation(LocalDate.parse("19920510", formatter));
            previousCompanyName.setDateOfNameEffectiveness(LocalDate.parse("19890101", formatter));
            previousCompanyName.setCompanyNumber(COMPANY_NUMBER);

            previousCompanyNames.add(previousCompanyName);
            company.setPreviousCompanyNames(previousCompanyNames);
        }

        if (includeMatchedPreviousName) {
            PreviousCompanyName previousCompanyName = new PreviousCompanyName();
            previousCompanyName.setName(PREVIOUS_COMPANY_NAME);
            previousCompanyName.setDateOfNameCessation(LocalDate.parse("19920510", formatter));
            previousCompanyName.setDateOfNameEffectiveness(LocalDate.parse("19890101", formatter));
            previousCompanyName.setCompanyNumber(COMPANY_NUMBER);

            company.setMatchedPreviousCompanyName(previousCompanyName);
        }

        return company;
    }

    private Company createAlphabeticalCompany() {
        Company company = new Company();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setCompanyType(COMPANY_TYPE);
        company.setOrderedAlphaKeyWithId(ORDERED_ALPHA_KEY_WITH_ID);
        company.setKind(KIND);

        Links links = new Links();
        links.setCompanyProfile(COMPANY_PROFILE_LINK);
        company.setLinks(links);

        return company;
    }

    private Company createAdvancedCompany() {
        Company company = new Company();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setCompanyType(COMPANY_TYPE);
        company.setSicCodes(SIC_CODES_LIST);
        company.setKind(KIND);

        company.setDateOfCessation(LocalDate.parse("20100501", formatter));
        company.setDateOfCreation(LocalDate.parse("19890501", formatter));

        Address address = new Address();
        address.setAddressLine1(ADDRESS_LINE_1);
        address.setAddressLine2(ADDRESS_LINE_2);
        address.setPostalCode(POSTCODE);
        address.setLocality(LOCALITY);
        company.setRegisteredOfficeAddress(address);

        Links links = new Links();
        links.setCompanyProfile(COMPANY_PROFILE_LINK);
        company.setLinks(links);

        return company;
    }
}
