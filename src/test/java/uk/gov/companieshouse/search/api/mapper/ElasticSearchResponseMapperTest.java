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

import uk.gov.companieshouse.search.api.model.DissolvedTopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.DissolvedCompany;
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
    private static final String KIND = "searchresults#dissolvedCompany";
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

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    @Test
    @DisplayName("Map dissolved response successful")
    void mapDissolvedResponseTest() {

        SearchHits searchHits = createSearchHits(true, true, true, true, true, true, true);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, dissolvedCompany.getCompanyName());
        assertEquals(COMPANY_NUMBER, dissolvedCompany.getCompanyNumber());
        assertEquals(COMPANY_STATUS, dissolvedCompany.getCompanyStatus());
        assertEquals(KIND, dissolvedCompany.getKind());
        assertEquals(DATE_OF_CESSATION, dissolvedCompany.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, dissolvedCompany.getDateOfCreation().toString());
        assertEquals(LOCALITY, dissolvedCompany.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, dissolvedCompany.getRegisteredOfficeAddress().getPostalCode());

        List<PreviousCompanyName> previousCompanyNames = dissolvedCompany.getPreviousCompanyNames();
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());
    }

    @Test
    @DisplayName("Map dissolved response successful date of cessation greater than 20 years old")
    void mapDissolvedResponseCessationGreaterThan20Test() {

        SearchHits searchHits = createSearchHits(true, true, true, true, true, true, false);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, dissolvedCompany.getCompanyName());
        assertEquals(COMPANY_NUMBER, dissolvedCompany.getCompanyNumber());
        assertEquals(COMPANY_STATUS, dissolvedCompany.getCompanyStatus());
        assertEquals(KIND, dissolvedCompany.getKind());
        assertEquals(DATE_OF_CESSATION_POST_20_YEARS, dissolvedCompany.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, dissolvedCompany.getDateOfCreation().toString());

        assertNull(dissolvedCompany.getRegisteredOfficeAddress());

        List<PreviousCompanyName> previousCompanyNames = dissolvedCompany.getPreviousCompanyNames();
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());
    }

    @Test
    @DisplayName("Map dissolved response no dates present successful")
    void mapDissolvedResponseNoDatesPresentTest() {

        SearchHits searchHits = createSearchHits(true, true,  true, true, true, false, false);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, dissolvedCompany.getCompanyName());
        assertEquals(COMPANY_NUMBER, dissolvedCompany.getCompanyNumber());
        assertEquals(COMPANY_STATUS, dissolvedCompany.getCompanyStatus());
        assertEquals(KIND, dissolvedCompany.getKind());
        assertNull(dissolvedCompany.getDateOfCessation());
        assertNull(dissolvedCompany.getDateOfCreation());

        List<PreviousCompanyName> previousCompanyNames = dissolvedCompany.getPreviousCompanyNames();
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());
    }

    @Test
    @DisplayName("Map dissolved response successful when no previous names")
    void mapDissolvedResponseTestPreviousNamesNull() {

        SearchHits searchHits = createSearchHits(true, true, true, true, false, true, true);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        List<PreviousCompanyName> previousCompanyNames = dissolvedCompany.getPreviousCompanyNames();
        assertNull(previousCompanyNames);
    }

    @Test
    @DisplayName("Map dissolved response successful when no address lines")
    void mapDissolvedResponseTestNoAddressLines() {

        SearchHits searchHits = createSearchHits(true, false, false, true, false, true, true);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = dissolvedCompany.getRegisteredOfficeAddress();
        assertEquals(POSTCODE, address.getPostalCode());
        assertNull(address.getLocality());
        assertNull(address.getAddressLine1());
        assertNull(address.getAddressLine2());
    }

    @Test
    @DisplayName("Map dissolved response successful when no locality")
    void mapDissolvedResponseTestNoLocality() {

        SearchHits searchHits = createSearchHits(true, true, false, true, false, true, true);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = dissolvedCompany.getRegisteredOfficeAddress();
        assertEquals(POSTCODE, address.getPostalCode());
        assertNull(address.getLocality());
    }

    @Test
    @DisplayName("Map dissolved response successful when no postcode")
    void mapDissolvedResponseTestNoPostcode() {

        SearchHits searchHits = createSearchHits(true, true, true, false, false, true, true);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = dissolvedCompany.getRegisteredOfficeAddress();
        assertEquals(LOCALITY, address.getLocality());
        assertNull(address.getPostalCode());
    }

    @Test
    @DisplayName("Map dissolved response successful when no address")
    void mapDissolvedResponseTestNoAddress() {

        SearchHits searchHits = createSearchHits(false, false,false, false, false, true, true);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        Address address = dissolvedCompany.getRegisteredOfficeAddress();
        assertNull(address);
    }

    @Test
    @DisplayName("Map dissolved top hit successful")
    void mapDissolvedTopHitSuccessful() {

        DissolvedTopHit topHit = elasticSearchResponseMapper.mapDissolvedTopHit(createDissolvedCompany(true, true, true));

        assertEquals(COMPANY_NAME, topHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, topHit.getCompanyNumber());
        assertEquals(COMPANY_STATUS, topHit.getCompanyStatus());
        assertEquals(KIND, topHit.getKind());
        assertEquals(DATE_OF_CESSATION, topHit.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, topHit.getDateOfCreation().toString());
        assertEquals(LOCALITY, topHit.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, topHit.getRegisteredOfficeAddress().getPostalCode());

        List<PreviousCompanyName> previousCompanyNames = topHit.getPreviousCompanyNames();
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());

        PreviousCompanyName matchedPreviousCompanyNames = topHit.getMatchedPreviousCompanyName();
        assertEquals(PREVIOUS_COMPANY_NAME, matchedPreviousCompanyNames.getName());
        assertEquals(EFFECTIVE_FROM, matchedPreviousCompanyNames.getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, matchedPreviousCompanyNames.getDateOfNameCessation().toString());
    }

    @Test
    @DisplayName("Map dissolved top hit successful no previous names")
    void mapDissolvedTopHitSuccessfulNoPreviousNames() {

        DissolvedTopHit topHit = elasticSearchResponseMapper.mapDissolvedTopHit(createDissolvedCompany(false, true, false));

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
    void mapDissolvedTopHitNoMatchingPreviousName() {

        DissolvedTopHit previousNamesTopHit = elasticSearchResponseMapper.mapDissolvedTopHit(createDissolvedCompany(true, true, false));

        assertEquals(COMPANY_NAME, previousNamesTopHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousNamesTopHit.getCompanyNumber());
        assertEquals(COMPANY_STATUS, previousNamesTopHit.getCompanyStatus());
        assertEquals(KIND, previousNamesTopHit.getKind());
        assertEquals(DATE_OF_CESSATION, previousNamesTopHit.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousNamesTopHit.getDateOfCreation().toString());
        assertEquals(LOCALITY, previousNamesTopHit.getRegisteredOfficeAddress().getLocality());
        assertEquals(POSTCODE, previousNamesTopHit.getRegisteredOfficeAddress().getPostalCode());

        List<PreviousCompanyName> previousCompanyNames = previousNamesTopHit.getPreviousCompanyNames();
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());

        assertNull(previousNamesTopHit.getMatchedPreviousCompanyName());
    }

    @Test
    @DisplayName("Map dissolved top hit successful no address")
    void mapDissolvedTopHitNoAddress() {

        DissolvedTopHit previousNamesTopHit = elasticSearchResponseMapper.mapDissolvedTopHit(createDissolvedCompany(false, false, false));

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

        List<DissolvedCompany> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        DissolvedCompany previousName = previousNames.get(0);
        assertEquals(COMPANY_NAME, previousName.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousName.getCompanyNumber());
        assertEquals(KIND, previousName.getKind());
        assertEquals(DATE_OF_CESSATION, previousName.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousName.getDateOfCreation().toString());
        assertEquals(POSTCODE, previousName.getRegisteredOfficeAddress().getPostalCode());
    }

    @Test
    @DisplayName("Map dissolved previous names successful no address object")
    void mapDissolvedPreviousNamesNoAddress() {

        SearchHits searchHits = createSearchHitsWithInnerHits(false, false, false, false, false);

        List<DissolvedCompany> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        DissolvedCompany previousName = previousNames.get(0);
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

        List<DissolvedCompany> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        DissolvedCompany previousName = previousNames.get(0);
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

        List<DissolvedCompany> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        DissolvedCompany previousName = previousNames.get(0);
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
        if(includeAddress) {
            searchHits.append(populateAddress(address, locality, postCode));
        }
        if(includePreviousCompanyNames) {
            searchHits.append(populatePreviousCompanyNames(includeDates));
        }
        if(includeDates) {
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
        SearchHit hit = new SearchHit( 1 );
        hit.sourceRef( source );

        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits( new SearchHit[] { hit }, totalHits, 10 );
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
        if(address) {
            addressJson.append("\"address_line_1\" : \"addressLine1\",");
            addressJson.append("\"address_line_2\" : \"addressLine2\"");
            if(locality || postCode) {
                addressJson.append(",");
            }
        }
        if(locality) {
            addressJson.append("\"locality\" : \"locality\"");
            if(postCode)
                addressJson.append(",");
        }
        if(postCode) {
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
                "\"ceased_on\" : \"19920510\"" +
                "}" +
                "]");

        if (includeDates) {
            previousNames.append(",");
        }

        return previousNames.toString();
    }

    private String createInnerHits() {
        String innerHits ="{" +
                "\"ordered_alpha_key\" : \"PREVIOUSNAME\"," +
                "\"effective_from\" : \"19980309\"," +
                "\"ceased_on\" : \"19990428\"," +
                "\"name\" : \"PREVIOUS NAME LIMITED\"" +
                "}";

        return innerHits;
    }

    private DissolvedCompany createDissolvedCompany(boolean includePreviousName, boolean includeAddress, boolean includeMatchedPreviousName) {
        DissolvedCompany dissolvedCompany = new DissolvedCompany();
        dissolvedCompany.setCompanyName(COMPANY_NAME);
        dissolvedCompany.setCompanyNumber(COMPANY_NUMBER);
        dissolvedCompany.setCompanyStatus(COMPANY_STATUS);
        dissolvedCompany.setKind(KIND);
        dissolvedCompany.setDateOfCessation(LocalDate.parse("20100501", formatter));
        dissolvedCompany.setDateOfCreation(LocalDate.parse("19890501", formatter));

        if (includeAddress) {
            Address address = new Address();
            address.setAddressLine1(ADDRESS_LINE_1);
            address.setAddressLine2(ADDRESS_LINE_2);
            address.setPostalCode(POSTCODE);
            address.setLocality(LOCALITY);
            dissolvedCompany.setRegisteredOfficeAddress(address);
        }

        if (includePreviousName) {
            List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
            PreviousCompanyName previousCompanyName = new PreviousCompanyName();
            previousCompanyName.setName(PREVIOUS_COMPANY_NAME);
            previousCompanyName.setDateOfNameCessation(LocalDate.parse("19920510", formatter));
            previousCompanyName.setDateOfNameEffectiveness(LocalDate.parse("19890101", formatter));

            previousCompanyNames.add(previousCompanyName);
            dissolvedCompany.setPreviousCompanyNames(previousCompanyNames);
        }

        if (includeMatchedPreviousName) {
            PreviousCompanyName previousCompanyName = new PreviousCompanyName();
            previousCompanyName.setName(PREVIOUS_COMPANY_NAME);
            previousCompanyName.setDateOfNameCessation(LocalDate.parse("19920510", formatter));
            previousCompanyName.setDateOfNameEffectiveness(LocalDate.parse("19890101", formatter));

            dissolvedCompany.setMatchedPreviousCompanyName(previousCompanyName);
        }

        return dissolvedCompany;
    }
}
