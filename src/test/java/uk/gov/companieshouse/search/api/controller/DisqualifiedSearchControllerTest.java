package uk.gov.companieshouse.search.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DELETE_NOT_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_DELETED;
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
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;
import uk.gov.companieshouse.search.api.service.upsert.disqualified.UpsertDisqualificationService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DisqualifiedSearchControllerTest {

    private final String OFFICER_ID = "12345encode";
    private final String PRIMARY_SEARCH_TYPE = "disqualified-officer";

    @Mock
    private ApiToResponseMapper mockApiToResponseMapper;

    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;

    @Mock
    private UpsertDisqualificationService upsertDisqualificationService;

    @Mock
    private PrimarySearchDeleteService primarySearchDeleteService;

    @InjectMocks
    private DisqualifiedSearchController disqualifiedSearchController;

    @Test
    @DisplayName("Test upsert returns a HTTP 200 Ok Response if the natural officer Id is presented")
    void testUpsertWithCorrectNaturalOfficerIdReturnsOkRequest() {

        testReturnsOkResponse("12345encode", false);
    }

    @Test
    @DisplayName("Test upsert returns a HTTP 200 Ok Response if the corporate officer Id is presented")
    void testUpsertWithCorrectCorporateOfficerIdReturnsOkRequest() {

        testReturnsOkResponse("12345encode", true);
    }

    @Test
    @DisplayName("Test upsert returns a HTTP 400 Bad request if the officer Id is an empty string")
    void testUpsertWithEmptyStringOfficerIdReturnsBadRequest() {

        testReturnsBadRequest("");
    }

    @Test
    @DisplayName("Test delete returns a HTTP 200 Ok Response if the  officer Id is presented")
    void testDeleteWithCorrectOfficerIdReturnsOkRequest() {
        when(primarySearchDeleteService.deleteOfficer(any()))
                .thenReturn(new ResponseObject(DOCUMENT_DELETED));
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = disqualifiedSearchController.deleteOfficer(OFFICER_ID);

        assertEquals(DOCUMENT_DELETED, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns a HTTP 404 Response if the officer Id is not presented")
    void testDeleteWithEmptyOfficerIdReturnsNotFound() {
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(NOT_FOUND).build());

        ResponseEntity<?> responseEntity = disqualifiedSearchController.deleteOfficer("");

        assertEquals(DELETE_NOT_FOUND, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    private void testReturnsOkResponse(String officerId, boolean corporate) {
        OfficerDisqualification officer = createOfficer(corporate);

        when(upsertDisqualificationService.upsertDisqualified(officer, officerId))
                .thenReturn(new ResponseObject(DOCUMENT_UPSERTED));
        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = disqualifiedSearchController.upsertOfficer(officerId, officer);

        assertEquals(DOCUMENT_UPSERTED, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    private void testReturnsBadRequest(String officerId) {
        OfficerDisqualification officer = createOfficer(false);

        when(mockApiToResponseMapper.map(responseObjectCaptor.capture()))
            .thenReturn(ResponseEntity.status(BAD_REQUEST).build());

        ResponseEntity<?> responseEntity = disqualifiedSearchController.upsertOfficer(officerId, officer);

        assertEquals(UPSERT_ERROR, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    private OfficerDisqualification createOfficer(boolean corporate) {
        OfficerDisqualification officer = new OfficerDisqualification();

        if (! corporate ) officer.setDateOfBirth(buildDateOfBirth(officer));

        officer.setKind("searchresults#disqualified-officer");

        officer.setItems(buildItems(officer, corporate));

        officer.setSortKey(officer.getItems().get(0).getWildcardKey());

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

    public List<Item> buildItems(OfficerDisqualification officer, boolean corporate) {
        Item item = new Item();
        List<Item> items = new ArrayList<>();

        if (corporate) {
            item.setCorporateName("Test Limited");
            item.setCorporateNameStart("Test");
            item.setCorporateNameEnding("Limited");
            item.setCorporateName("Limited");
            item.setWildcardKey("Test Limited1");
        } else {
            item.setPersonName("Thomas Lee SAMSON");
            item.setForename("Thomas");
            item.setOtherForenames("SAMSON Thomas Lee");
            item.setSurname("SAMSON");
            item.setWildcardKey("SAMSON Thomas Lee1");
        }
        item.setRecordType("disqualifications");
        item.setAddress(buildAddress());
        item.setDisqualifiedFrom("2020-08-16");
        item.setDisqualifiedUntil("2026-08-16");
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