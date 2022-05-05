package uk.gov.companieshouse.search.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.disqualification.DateOfBirth;
import uk.gov.companieshouse.api.disqualification.DisqualificationLinks;
import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.api.model.delta.officers.AddressAPI;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.upsert.disqualified.UpsertDisqualificationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DisqualifiedSearchControllerTest {

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;

    @Mock
    private UpsertDisqualificationService upsertDisqualificationService;

    @InjectMocks
    private DisqualifiedSearchController disqualifiedSearchController;

    @Test
    @DisplayName("Test upsert returns a HTTP 200 Ok Response if the officer Id is presented")
    void testUpsertWithCorrectOfficerIdReturnsOkRequest() {

        testReturnsOkResponse("12345encode");
    }

    @Test
    @DisplayName("Test upsert returns a HTTP 400 Bad request if the officer Id is an empty string")
    void testUpsertWithEmptyStringOfficerIdReturnsBadRequest() {

        testReturnsBadRequest("");
    }

    private void testReturnsOkResponse(String officerId) {
        OfficerDisqualification officer = createOfficer();

        when(upsertDisqualificationService.upsertNaturalDisqualified(officer, officerId))
                .thenReturn(new ResponseObject(DOCUMENT_UPSERTED));
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = disqualifiedSearchController.upsertOfficer(officerId, officer);

        assertEquals(DOCUMENT_UPSERTED, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    private void testReturnsBadRequest(String officerId) {
        OfficerDisqualification officer = createOfficer();

        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
            .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = disqualifiedSearchController.upsertOfficer(officerId, officer);

        assertEquals(UPSERT_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    private OfficerDisqualification createOfficer() {
        OfficerDisqualification officer = new OfficerDisqualification();

        officer.setDateOfBirth(buildDateOfBirth(officer));

        officer.setKind("searchresults#disqualified-officer");

        officer.setItems(buildItems(officer));

        officer.setSortKey(officer.getItems().get(0).getSurname() + " " +
                officer.getItems().get(0).getForename() + " " +
                officer.getItems().get(0).getOtherForenames() + 1);

        DisqualificationLinks links = new DisqualificationLinks();
        links.setSelf("/disqualified-officers/natural/12345encode");
        officer.setLinks(links);

        return officer;
    }

    public DateOfBirth buildDateOfBirth(OfficerDisqualification officer) {
        DateOfBirth dateOfBirth = new DateOfBirth();
        dateOfBirth.setYear("1990");
        dateOfBirth.setMonth("06");
        dateOfBirth.setDay("01");

        return dateOfBirth;
    }

    public List<Item> buildItems(OfficerDisqualification officer) {
        Item item = new Item();
        List<Item> items = new ArrayList<>();

        String disqualifiedFrom = "2020-08-16";
        String disqualifiedUntil =  "2026-08-16";
        LocalDate disqualifiedFromDate = LocalDate.parse(disqualifiedFrom);
        LocalDate disqualifiedUntilDate = LocalDate.parse(disqualifiedUntil);

        item.setPersonName("Thomas Lee SAMSON");
        item.setForename("Thomas");
        item.setRecordType("disqualifications");
        item.setAddress(buildAddress());
        item.setDisqualifiedFrom(disqualifiedFromDate);
        item.setDisqualifiedUntil(disqualifiedUntilDate);
        item.setOtherForenames("SAMSON Thomas Lee1");
        item.setSurname("SAMSON");
        item.setFullAddress("1 Street, Castle, Castle, King County, King, KE1 1NN");

        items.add(item);

        return items;
    }

    public Object buildAddress() {
        AddressAPI address = new AddressAPI();

        address.setAddressLine1("Street");
        address.setAddressLine2("Castle");
        address.setCountry("King");
        address.setPremises("1");
        address.setLocality("Castle");
        address.setPostcode("KE1 1NN");
        address.setRegion("King County");

        return address;
    }
}