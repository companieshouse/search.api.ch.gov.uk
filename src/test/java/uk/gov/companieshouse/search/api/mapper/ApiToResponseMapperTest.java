package uk.gov.companieshouse.search.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.REQUEST_PARAMETER_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_NOT_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiToResponseMapperTest {

    @InjectMocks
    private ApiToResponseMapper apiToResponseMapper;

    @Test
    @DisplayName("Test if OK returned")
    void testFoundReturned() {

        DissolvedResponseObject responseObject =
            new DissolvedResponseObject(SEARCH_FOUND, new DissolvedSearchResults());

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Not Found returned")
    void testNotFoundReturned() {

        DissolvedResponseObject responseObject =
            new DissolvedResponseObject(SEARCH_NOT_FOUND);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if OK returned")
    void testOKReturned() {

        DissolvedResponseObject responseObject =
            new DissolvedResponseObject(DOCUMENT_UPSERTED);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Bad Request returned on update request error")
    void testBadRequestOnUpdateReturned() {

        DissolvedResponseObject responseObject =
            new DissolvedResponseObject(UPDATE_REQUEST_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    @DisplayName("Test if Bad Request returned on upsert error")
    void testBadRequestOnUpsertReturned() {

        DissolvedResponseObject responseObject =
            new DissolvedResponseObject(UPSERT_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Internal Server Error returned")
    void testInternalServerErrorReturned() {

        DissolvedResponseObject responseObject =
            new DissolvedResponseObject(SEARCH_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Search found returned for dissolved company")
    void testFoundReturnedDissolved() {

        DissolvedResponseObject responseObject =
                new DissolvedResponseObject(SEARCH_FOUND, new DissolvedSearchResults());

        ResponseEntity responseEntity = apiToResponseMapper.mapDissolved(responseObject);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Not Found returned for dissolved company")
    void testNotFoundReturnedDissolved() {

        DissolvedResponseObject responseObject =
                new DissolvedResponseObject(SEARCH_NOT_FOUND);

        ResponseEntity responseEntity = apiToResponseMapper.mapDissolved(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Request Parameter Error returned for dissolved company")
    void testRequestParamErrorReturnedDissolved() {

        DissolvedResponseObject responseObject =
                new DissolvedResponseObject(REQUEST_PARAMETER_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.mapDissolved(responseObject);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Invalid url parameter for search_type, please try 'alphabetical', 'best-match' or 'previous-name-dissolved'", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test if Internal Server Error returned for dissolved company")
    void testInternalServerErrorReturnedDissolved() {

        DissolvedResponseObject responseObject =
                new DissolvedResponseObject(SEARCH_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.mapDissolved(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}