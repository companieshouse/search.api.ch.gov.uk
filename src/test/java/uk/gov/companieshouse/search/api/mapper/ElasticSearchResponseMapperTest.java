package uk.gov.companieshouse.search.api.mapper;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    @Test
    @DisplayName("Map alphabetical response successful")
    void mapAlphabeticalResponseTest() {

        SearchHits searchHits = createAlphabeticalSearchHits();

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

        SearchHits searchHits = createSearchHits(true, true, true, true, true, true, true);

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

        SearchHits searchHits = createSearchHits(true, true, true, true, true, true, false);

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

        SearchHits searchHits = createSearchHits(true, true, true, true, true, false, false);

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

        SearchHits searchHits = createSearchHits(true, true, true, true, false, true, true);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        List<PreviousCompanyName> previousCompanyNames = company.getPreviousCompanyNames();
        assertNull(previousCompanyNames);
    }

    @Test
    @DisplayName("Map dissolved response successful when no address lines")
    void mapDissolvedResponseTestNoAddressLines() {

        SearchHits searchHits = createSearchHits(true, false, false, true, false, true, true);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = company.getRegisteredOfficeAddress();
        assertEquals(POSTCODE, address.getPostalCode());
        assertNull(address.getLocality());
        assertNull(address.getAddressLine1());
        assertNull(address.getAddressLine2());
    }

    @Test
    @DisplayName("Map dissolved response successful when no locality")
    void mapDissolvedResponseTestNoLocality() {

        SearchHits searchHits = createSearchHits(true, true, false, true, false, true, true);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = company.getRegisteredOfficeAddress();
        assertEquals(POSTCODE, address.getPostalCode());
        assertNull(address.getLocality());
    }

    @Test
    @DisplayName("Map dissolved response successful when no postcode")
    void mapDissolvedResponseTestNoPostcode() {

        SearchHits searchHits = createSearchHits(true, true, true, false, false, true, true);

        Company company =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = company.getRegisteredOfficeAddress();
        assertEquals(LOCALITY, address.getLocality());
        assertNull(address.getPostalCode());
    }

    @Test
    @DisplayName("Map dissolved response successful when no address")
    void mapDissolvedResponseTestNoAddress() {

        SearchHits searchHits = createSearchHits(false, false, false, false, false, true, true);

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

    private void assertPreviousCompanyName(List<PreviousCompanyName> previousCompanyNames) {
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());
        assertEquals(COMPANY_NUMBER, previousCompanyNames.get(0).getCompanyNumber());
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

        SearchHits searchHits = createSearchHitsWithInnerHits(true, true, true, true, true);

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

        SearchHits searchHits = createSearchHitsWithInnerHits(false, false, false, false, false);

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

        SearchHits searchHits = createSearchHitsWithInnerHits(true, true, false, false, false);

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
    @DisplayName("Map dissolved previous names successful no postal code")
    void mapDissolvedPreviousNamesNoPreviousNames() {

        SearchHits searchHits = createSearchHitsWithInnerHits(true, true, true, true, false);

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

    private SearchHits createSearchHits(boolean includeAddress,
                                        boolean address,
                                        boolean locality,
                                        boolean postCode,
                                        boolean includePreviousCompanyNames,
                                        boolean includeDates,
                                        boolean pre20Years) {
        StringBuilder searchHits = new StringBuilder();
        searchHits.append(
                "{" +
                        "\"company_number\" : \"00000000\"," +
                        "\"company_name\" : \"TEST COMPANY\"," +
                        "\"alpha_key\": \"alpha_key\"," +
                        "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                        "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," +
                        "\"company_status\" : \"dissolved\",");
        if (includeAddress) {
            searchHits.append(populateAddress(address, locality, postCode));
        }
        if (includePreviousCompanyNames) {
            searchHits.append(populatePreviousCompanyNames(includeDates));
        }
        if (includeDates) {
            if (pre20Years) {
                searchHits.append(
                        "\"date_of_cessation\" : \"20100501\"," +
                                "\"date_of_creation\" : \"19890501\"" +
                                "}");
            } else {
                searchHits.append(
                        "\"date_of_cessation\" : \"19910501\"," +
                                "\"date_of_creation\" : \"19890501\"" +
                                "}");
            }
        } else {
            searchHits.append("}");
        }
        BytesReference source = new BytesArray(searchHits.toString());
        SearchHit hit = new SearchHit(1);
        hit.sourceRef(source);

        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits(new SearchHit[]{hit}, totalHits, 10);
    }

    private SearchHits createSearchHitsWithInnerHits(boolean includeAddress,
                                                     boolean includeAddressLines,
                                                     boolean includeLocality,
                                                     boolean includePostCode,
                                                     boolean includePreviousCompanyNames) {

        SearchHits searchHits = createSearchHits(includeAddress, includeAddressLines, includeLocality, includePostCode, includePreviousCompanyNames, true, true);
        SearchHit searchHit = searchHits.getAt(0);

        Map<String, SearchHits> innerHits = new HashMap<>();

        BytesReference source = new BytesArray(createInnerHits());
        SearchHit innerHit = new SearchHit(100);
        innerHit.sourceRef(source);

        innerHits.put("previous_company_names", new SearchHits(new SearchHit[]{innerHit}, new TotalHits(1, TotalHits.Relation.EQUAL_TO), 1f));

        searchHit.setInnerHits(innerHits);

        return searchHits;

    }

    private String populateAddress(boolean address, boolean locality, boolean postCode) {
        StringBuilder addressJson = new StringBuilder("\"registered_office_address\" : {");
        if (address) {
            addressJson.append("\"address_line_1\" : \"addressLine1\",");
            addressJson.append("\"address_line_2\" : \"addressLine2\"");
            if (locality || postCode) {
                addressJson.append(",");
            }
        }
        if (locality) {
            addressJson.append("\"locality\" : \"locality\"");
            if (postCode)
                addressJson.append(",");
        }
        if (postCode) {
            addressJson.append("\"post_code\" : \"AB00 0 AB\"");
        }
        addressJson.append("},");
        return addressJson.toString();
    }

    private String populatePreviousCompanyNames(boolean includeDates) {
        StringBuilder previousNames = new StringBuilder();

        previousNames.append("\"previous_company_names\" : [" +
                "{" +
                "\"name\" : \"TEST COMPANY 2\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"effective_from\" : \"19890101\"," +
                "\"ceased_on\" : \"19920510\"," +
                "\"company_number\" : \"00000000\"" +
                "}" +
                "]");

        if (includeDates) {
            previousNames.append(",");
        }

        return previousNames.toString();
    }

    private String createInnerHits() {
        String innerHits = "{" +
                "\"ordered_alpha_key\" : \"PREVIOUSNAME\"," +
                "\"effective_from\" : \"19980309\"," +
                "\"ceased_on\" : \"19990428\"," +
                "\"name\" : \"PREVIOUS NAME LIMITED\"," +
                "\"company_number\" : \"00000000\"" +
                "}";

        return innerHits;
    }

    private SearchHits createAlphabeticalSearchHits() {
        BytesReference source = new BytesArray("{" + "\"ID\": \"id\"," + "\"company_type\": \"ltd\","
                + "\"ordered_alpha_key_with_id\": \"ordered_alpha_key_with_id\"," + "\"items\" : {"
                + "\"company_number\" : \"00000000\"," + "\"company_status\" : \"active\","
                + "\"corporate_name\" : \"TEST COMPANY\"" + "}," + "\"links\" : {" + "\"self\" : \"/company/00000000\"" + "}" + "}");
        SearchHit hit = new SearchHit(1);
        hit.sourceRef(source);
        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits(new SearchHit[] { hit }, totalHits, 10);
    }

    private Company createCompany(boolean includePreviousName, boolean includeAddress, boolean includeMatchedPreviousName) {
        Company company = new Company();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);
        company.setKind(KIND);
        company.setDateOfCessation(LocalDate.parse("20100501", formatter));
        company.setDateOfCreation(LocalDate.parse("19890501", formatter));

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
}
