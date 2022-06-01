package uk.gov.companieshouse.search.api.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.disqualification.DateOfBirth;
import uk.gov.companieshouse.api.disqualification.DisqualificationLinks;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.exception.UpsertException;

public class DisqualifiedSearchUpsertRequestTest {

    private static final String FORENAME = "forename";
    private static final String SURNAME = "surname";
    private static final String SELF = "links/natural/asdcadc";
    private static final String SORT_KEY = "key";
    private static final String KIND = "searchresults#disqualified-officer";

    private DisqualifiedSearchUpsertRequest request = new DisqualifiedSearchUpsertRequest();

    @Test
    void officerIsTransformedToJSONString() throws Exception {
        String actual = request.buildRequest(createOfficer(true, true, true));

        String expected = createExpectedJSON();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode actualNode = mapper.readTree(actual);
        JsonNode expectedNode = mapper.readTree(expected);

        assertEquals(expectedNode, actualNode);
    }

    @Test
    void missingKindOfficerIsNotTransformedToJSONString() throws Exception {
        assertThrows(UpsertException.class,
            () -> request.buildRequest(createOfficer(false, true, true)));
    }

    @Test
    void missingSelfOfficerIsNotTransformedToJSONString() throws Exception {
        assertThrows(UpsertException.class,
            () -> request.buildRequest(createOfficer(true, false, true)));
    }

    @Test
    void missingAddressOfficerIsNotTransformedToJSONString() throws Exception {
        assertThrows(UpsertException.class,
            () -> request.buildRequest(createOfficer(true, true, false)));
    }

    @Test
    void incorrectSelfOfficerIsNotTransformedToJSONString() throws Exception {
        OfficerDisqualification officer = createOfficer(true, true, true);
        officer.getLinks().setSelf("links");
        assertThrows(UpsertException.class,
            () -> request.buildRequest(officer));
    }

    @Test
    void incorrectKindOfficerIsNotTransformedToJSONString() throws Exception {
        OfficerDisqualification officer = createOfficer(true, true, true);
        officer.setKind(" ");
        assertThrows(UpsertException.class,
            () -> request.buildRequest(officer));
    }

    private OfficerDisqualification createOfficer(boolean kindPasses, boolean selfPasses, boolean addressPasses) throws Exception{
        OfficerDisqualification officer = new OfficerDisqualification();
        Item item = new Item();
        item.setForename(FORENAME);
        item.setSurname(SURNAME);
        item.setDisqualifiedFrom("2020-01-01");
        item.setDisqualifiedUntil("2025-01-01");
        if (addressPasses) {
            item.setAddress(createAddress().toString());
        }
        officer.addItemsItem(item);
        DateOfBirth dob = new DateOfBirth();
        dob.setYear("2000");
        dob.setMonth("01");
        dob.setDay("01");
        officer.setDateOfBirth(dob);
        DisqualificationLinks links = new DisqualificationLinks();
        if (selfPasses) {
            links.setSelf(SELF);
        }
        officer.setLinks(links);
        if (kindPasses) {
            officer.setKind(KIND);
        }
        officer.setSortKey(SORT_KEY);
        return officer;
    }

    private String createExpectedJSON() throws Exception {
        JSONObject address = createAddress();
        JSONObject item = new JSONObject()
                .put("forename", FORENAME)
                .put("surname", SURNAME)
                .put("disqualified_from", "2020-01-01")
                .put("disqualified_until", "2025-01-01")
                .put("address", address.toString());
        JSONObject dob = new JSONObject()
                .put("year", "2000")
                .put("month", "01")
                .put("day", "01");
        JSONObject links = new JSONObject()
                .put("self", SELF);
        JSONObject officer = new JSONObject()
                .put("items", new JSONArray().put(item))
                .put("date_of_birth", dob)
                .put("links", links)
                .put("sort_key", SORT_KEY)
                .put("kind", KIND);
        return officer.toString();
    }

    private JSONObject createAddress() throws Exception {
        JSONObject address = new JSONObject()
        .put("postcode", "postcode")
        .put("address_line_1", "addressLine1");
        return address;
    }
}
