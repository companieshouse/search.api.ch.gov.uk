package uk.gov.companieshouse.search.api.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.disqualification.DateOfBirth;
import uk.gov.companieshouse.api.disqualification.DisqualificationLinks;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;

import java.time.LocalDate;

public class DisqualifiedSearchUpsertRequestTest {

    private static final String FORENAME = "forename";
    private static final String SURNAME = "surname";
    private static final String SELF = "links";
    private static final String SORT_KEY = "key";

    private DisqualifiedSearchUpsertRequest request = new DisqualifiedSearchUpsertRequest();

    @Test
    public void officerIsTransformedToJSONString() throws Exception {
        String actual = request.buildRequest(createOfficer());

        String expected = createExpectedJSON();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode actualNode = mapper.readTree(actual);
        JsonNode expectedNode = mapper.readTree(expected);

        assertEquals(expectedNode, actualNode);
    }

    private OfficerDisqualification createOfficer() {
        OfficerDisqualification officer = new OfficerDisqualification();
        Item item = new Item();
        item.setForename(FORENAME);
        item.setSurname(SURNAME);
        item.setDisqualifiedFrom(LocalDate.of(2020, 1, 1));
        item.setDisqualifiedUntil(LocalDate.of(2025, 1, 1));
        officer.addItemsItem(item);
        DateOfBirth dob = new DateOfBirth();
        dob.setYear("2000");
        dob.setMonth("01");
        dob.setDay("01");
        officer.setDateOfBirth(dob);
        DisqualificationLinks links = new DisqualificationLinks();
        links.setSelf(SELF);
        officer.setLinks(links);
        officer.setSortKey(SORT_KEY);
        return officer;
    }

    private String createExpectedJSON() throws Exception {
        JSONObject item = new JSONObject()
                .put("forename", FORENAME)
                .put("surname", SURNAME)
                .put("disqualified_from", "2020-01-01")
                .put("disqualified_until", "2025-01-01");
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
                .put("sort_key", SORT_KEY);
        return officer.toString();
    }
}
