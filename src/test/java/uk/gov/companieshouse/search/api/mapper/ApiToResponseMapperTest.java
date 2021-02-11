package uk.gov.companieshouse.search.api.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_NOT_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiToResponseMapperTest {

    @InjectMocks
    private ApiToResponseMapper apiToResponseMapper;

    @Test
    @DisplayName("Test if OK returned")
    void testFoundReturned() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_FOUND, new SearchResults());

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Not Found returned")
    void testNotFoundReturned() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_NOT_FOUND);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if OK returned")
    void testOKReturned() {

        ResponseObject responseObject =
            new ResponseObject(DOCUMENT_UPSERTED);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Bad Request returned on update request error")
    void testBadRequestOnUpdateReturned() {

        ResponseObject responseObject =
            new ResponseObject(UPDATE_REQUEST_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    @DisplayName("Test if Bad Request returned on upsert error")
    void testBadRequestOnUpsertReturned() {

        ResponseObject responseObject =
            new ResponseObject(UPSERT_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Internal Server Error returned")
    void testInternalServerErrorReturned() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

}