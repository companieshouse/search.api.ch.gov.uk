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
import uk.gov.companieshouse.search.api.model.PreviousNamesTopHit;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.DissolvedCompany;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.PreviousCompanyName;
import uk.gov.companieshouse.search.api.model.esdatamodel.dissolved.previousnames.DissolvedPreviousName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.apache.lucene.search.TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ElasticSearchResponseMapperTest {

    @InjectMocks
    private ElasticSearchResponseMapper elasticSearchResponseMapper;


    private static final String COMPANY_NAME = "TEST COMPANY";
    private static final String COMPANY_NUMBER = "00000000";
    private static final String COMPANY_STATUS = "dissolved";
    private static final String KIND = "searchresults#dissolvedCompany";
    private static final String DATE_OF_CESSATION = "1999-05-01";
    private static final String DATE_OF_CREATION = "1989-05-01";
    private static final String LOCALITY = "locality";
    private static final String POSTCODE = "AB00 0 AB";
    private static final String PREVIOUS_COMPANY_NAME = "TEST COMPANY 2";
    private static final String EFFECTIVE_FROM = "1989-01-01";
    private static final String CEASED_ON = "1992-05-10";
    private static final String DISSOLVED_PREVIOUS_NAME = "PREVIOUS NAME LIMITED";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);

    @Test
    @DisplayName("Map dissolved response successful")
    void mapDissolvedResponseTest() {

        SearchHits searchHits = createSearchHits(true, true, true, true);

        DissolvedCompany dissolvedCompany =
                elasticSearchResponseMapper.mapDissolvedResponse(searchHits.getAt(0));

        assertEquals(COMPANY_NAME, dissolvedCompany.getCompanyName());
        assertEquals(COMPANY_NUMBER, dissolvedCompany.getCompanyNumber());
        assertEquals(COMPANY_STATUS, dissolvedCompany.getCompanyStatus());
        assertEquals(KIND, dissolvedCompany.getKind());
        assertEquals(DATE_OF_CESSATION, dissolvedCompany.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, dissolvedCompany.getDateOfCreation().toString());
        assertEquals(LOCALITY, dissolvedCompany.getAddress().getLocality());
        assertEquals(POSTCODE, dissolvedCompany.getAddress().getPostalCode());

        List<PreviousCompanyName> previousCompanyNames = dissolvedCompany.getPreviousCompanyNames();
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());
    }

    @Test
    @DisplayName("Map dissolved top hit successful")
    void mapDissolvedTopHitSuccessful() {

        DissolvedTopHit dissolvedCompany = new DissolvedTopHit();

        elasticSearchResponseMapper.mapDissolvedTopHit(dissolvedCompany, createDissolvedCompany());

        assertEquals(COMPANY_NAME, dissolvedCompany.getCompanyName());
        assertEquals(COMPANY_NUMBER, dissolvedCompany.getCompanyNumber());
        assertEquals(COMPANY_STATUS, dissolvedCompany.getCompanyStatus());
        assertEquals(KIND, dissolvedCompany.getKind());
        assertEquals(DATE_OF_CESSATION, dissolvedCompany.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, dissolvedCompany.getDateOfCreation().toString());
        assertEquals(LOCALITY, dissolvedCompany.getAddress().getLocality());
        assertEquals(POSTCODE, dissolvedCompany.getAddress().getPostalCode());

        List<PreviousCompanyName> previousCompanyNames = dissolvedCompany.getPreviousCompanyNames();
        assertEquals(PREVIOUS_COMPANY_NAME, previousCompanyNames.get(0).getName());
        assertEquals(EFFECTIVE_FROM, previousCompanyNames.get(0).getDateOfNameEffectiveness().toString());
        assertEquals(CEASED_ON, previousCompanyNames.get(0).getDateOfNameCessation().toString());
    }

    @Test
    @DisplayName("Map dissolved previous names top hit successful")
    void mapDissolvedPreviousNamesTopHit() {

        PreviousNamesTopHit previousNamesTopHit = new PreviousNamesTopHit();

        elasticSearchResponseMapper.mapPreviousNamesTopHit(createPreviousCompanyNames(), previousNamesTopHit);

        assertEquals(COMPANY_NAME, previousNamesTopHit.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousNamesTopHit.getCompanyNumber());
        assertEquals(COMPANY_STATUS, previousNamesTopHit.getCompanyStatus());
        assertEquals(KIND, previousNamesTopHit.getKind());
        assertEquals(DATE_OF_CESSATION, previousNamesTopHit.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousNamesTopHit.getDateOfCreation().toString());
        assertEquals(LOCALITY, previousNamesTopHit.getAddress().getLocality());
        assertEquals(POSTCODE, previousNamesTopHit.getAddress().getPostalCode());
    }

    @Test
    @DisplayName("Map dissolved previous names successful")
    void mapDissolvedPreviousNames() {

        SearchHits searchHits = createSearchHitsWithInnerHits(true);

        List<DissolvedPreviousName> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        DissolvedPreviousName previousName = previousNames.get(0);
        assertEquals(DISSOLVED_PREVIOUS_NAME, previousName.getDissolvedPreviousName());
        assertEquals(COMPANY_NAME, previousName.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousName.getCompanyNumber());
        assertEquals(COMPANY_STATUS, previousName.getCompanyStatus());
        assertEquals(KIND, previousName.getKind());
        assertEquals(DATE_OF_CESSATION, previousName.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousName.getDateOfCreation().toString());
        assertEquals(POSTCODE, previousName.getAddress().getPostalCode());
    }

    @Test
    @DisplayName("Map dissolved previous names successful no address object")
    void mapDissolvedPreviousNamesNoAddress() {

        SearchHits searchHits = createSearchHitsWithInnerHits(false);

        List<DissolvedPreviousName> previousNames =
                elasticSearchResponseMapper.mapPreviousNames(searchHits);

        DissolvedPreviousName previousName = previousNames.get(0);
        assertEquals(DISSOLVED_PREVIOUS_NAME, previousName.getDissolvedPreviousName());
        assertEquals(COMPANY_NAME, previousName.getCompanyName());
        assertEquals(COMPANY_NUMBER, previousName.getCompanyNumber());
        assertEquals(COMPANY_STATUS, previousName.getCompanyStatus());
        assertEquals(KIND, previousName.getKind());
        assertEquals(DATE_OF_CESSATION, previousName.getDateOfCessation().toString());
        assertEquals(DATE_OF_CREATION, previousName.getDateOfCreation().toString());
    }

    private SearchHits createSearchHits(boolean includeAddress,
                                        boolean locality,
                                        boolean postCode,
                                        boolean includePreviousCompanyNames) {
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
            searchHits.append(populateAddress(locality, postCode));
        }
        if(includePreviousCompanyNames) {
            searchHits.append(populatePreviousCompanyNames());
        }
        searchHits.append(
                    "\"date_of_cessation\" : \"19990501\"," +
                    "\"date_of_creation\" : \"19890501\"" +
                "}");
        BytesReference source = new BytesArray(searchHits.toString());
        SearchHit hit = new SearchHit( 1 );
        hit.sourceRef( source );

        TotalHits totalHits = new TotalHits(1, GREATER_THAN_OR_EQUAL_TO);
        return new SearchHits( new SearchHit[] { hit }, totalHits, 10 );
    }

    private SearchHits createSearchHitsWithInnerHits(boolean includePostcode) {

        SearchHits searchHits = createSearchHits(true, true, includePostcode, false);
        SearchHit searchHit = searchHits.getAt(0);

        Map<String, SearchHits> innerHits = new HashMap<>();

        BytesReference source = new BytesArray(createInnerHits());
        SearchHit innerHit = new SearchHit(100);
        innerHit.sourceRef(source);

        innerHits.put("previous_company_names", new SearchHits(new SearchHit[]{innerHit}, new TotalHits(1, TotalHits.Relation.EQUAL_TO), 1f));

        searchHit.setInnerHits(innerHits);

        return searchHits;

    }

    private String populateAddress(boolean locality, boolean postCode) {
        StringBuilder address = new StringBuilder("\"address\" : {");
        if(locality) {
            address.append("\"locality\" : \"locality\"");
            if(postCode)
                address.append(",");
        }
        if(postCode) {
            address.append("\"postal_code\" : \"AB00 0 AB\"");
        }
        address.append("},");
        return address.toString();
    }

    private String populatePreviousCompanyNames() {
        String previousNames = "\"previous_company_names\" : [" +
                "{" +
                "\"name\" : \"TEST COMPANY 2\"," +
                "\"ordered_alpha_key\": \"ordered_alpha_key\"," +
                "\"effective_from\" : \"19890101\"," +
                "\"ceased_on\" : \"19920510\"" +
                "}" +
                "],";
        return previousNames;
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

    private DissolvedCompany createDissolvedCompany() {
        DissolvedCompany dissolvedCompany = new DissolvedCompany();
        dissolvedCompany.setCompanyName(COMPANY_NAME);
        dissolvedCompany.setCompanyNumber(COMPANY_NUMBER);
        dissolvedCompany.setCompanyStatus(COMPANY_STATUS);
        dissolvedCompany.setKind(KIND);
        dissolvedCompany.setDateOfCessation(LocalDate.parse("19990501", formatter));
        dissolvedCompany.setDateOfCreation(LocalDate.parse("19890501", formatter));

        Address address = new Address();
        address.setPostalCode(POSTCODE);
        address.setLocality(LOCALITY);
        dissolvedCompany.setAddress(address);

        List<PreviousCompanyName> previousCompanyNames = new ArrayList<>();
        PreviousCompanyName previousCompanyName = new PreviousCompanyName();
        previousCompanyName.setName(PREVIOUS_COMPANY_NAME);
        previousCompanyName.setDateOfNameCessation(LocalDate.parse("19920510", formatter));
        previousCompanyName.setDateOfNameEffectiveness(LocalDate.parse("19890101", formatter));

        previousCompanyNames.add(previousCompanyName);
        dissolvedCompany.setPreviousCompanyNames(previousCompanyNames);

        return dissolvedCompany;
    }

    private List<DissolvedPreviousName> createPreviousCompanyNames() {
        List<DissolvedPreviousName> previousNames = new ArrayList<>();
        DissolvedPreviousName previousName = new DissolvedPreviousName();
        previousName.setDissolvedPreviousName(PREVIOUS_COMPANY_NAME);
        previousName.setCompanyName(COMPANY_NAME);
        previousName.setCompanyNumber(COMPANY_NUMBER);
        previousName.setCompanyStatus(COMPANY_STATUS);
        previousName.setKind(KIND);
        previousName.setDateOfCessation(LocalDate.parse("19990501", formatter));
        previousName.setDateOfCreation(LocalDate.parse("19890501", formatter));

        Address address = new Address();
        address.setPostalCode(POSTCODE);
        address.setLocality(LOCALITY);
        previousName.setAddress(address);

        previousNames.add(previousName);

        return previousNames;

    }
}
