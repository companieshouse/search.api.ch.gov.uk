package uk.gov.companieshouse.search.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DELETE_NOT_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_DELETED;

@ExtendWith(MockitoExtension.class)
class OfficersSearchControllerTest {
    private final String OFFICER_ID = "ABCD1234";
    @Mock
    private ApiToResponseMapper apiToResponseMapper;

    @Captor
    private ArgumentCaptor<ResponseObject> responseObjectCaptor;
    @Mock
    private PrimarySearchDeleteService primarySearchDeleteService;

    @InjectMocks
    private OfficersSearchController officersSearchController;
    @Test
    @DisplayName("Test delete returns HTTP 200 OK given officer exist in index")
    void testDeleteWithCorrectOfficerIdReturnsOkRequest() {
        when(primarySearchDeleteService.deleteOfficer(any()))
                .thenReturn(new ResponseObject(DOCUMENT_DELETED));
        when(apiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(OK).build());

        ResponseEntity<?> responseEntity = officersSearchController.deleteOfficer(OFFICER_ID);

        assertEquals(DOCUMENT_DELETED, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test delete returns HTTP 404 Not Found if path variable: officer_id is missing")
    void testDeleteWithMissingParameterOfficerIdReturnsNotFound() {
        when(apiToResponseMapper.map(responseObjectCaptor.capture()))
                .thenReturn(ResponseEntity.status(NOT_FOUND).build());

        ResponseEntity<?> responseEntity = officersSearchController.deleteOfficer("");

        assertEquals(DELETE_NOT_FOUND, responseObjectCaptor.getValue().getStatus());
        assertNotNull(responseEntity);
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }
}
