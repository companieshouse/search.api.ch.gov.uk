package uk.gov.companieshouse.search.api.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DATE_FORMAT_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.DOCUMENT_UPSERTED;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.REQUEST_PARAMETER_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SEARCH_NOT_FOUND;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.SIZE_PARAMETER_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPDATE_REQUEST_ERROR;
import static uk.gov.companieshouse.search.api.model.response.ResponseStatus.UPSERT_ERROR;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiToResponseMapperTest {

    @InjectMocks
    private ApiToResponseMapper apiToResponseMapper;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    private static final String MAX_SIZE_PARAM = "MAX_SIZE_PARAM";

    @Test
    @DisplayName("Test if OK returned")
    void testFoundReturned() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_FOUND, new SearchResults());

        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if OK returned")
    void testOKReturned() {

        ResponseObject responseObject =
            new ResponseObject(DOCUMENT_UPSERTED);

        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Not Found returned")
    void testNotFoundReturned() {

        ResponseObject responseObject =
                new ResponseObject(SEARCH_NOT_FOUND);

        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Bad Request returned on update request error")
    void testBadRequestOnUpdateReturned() {

        ResponseObject responseObject =
            new ResponseObject(UPDATE_REQUEST_ERROR);

        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    @DisplayName("Test if Bad Request returned on upsert error")
    void testBadRequestOnUpsertReturned() {

        ResponseObject responseObject =
            new ResponseObject(UPSERT_ERROR);

        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Bad Request returned on date format error")
    void testBadRequestOnDateFormatErrorReturned() {

        ResponseObject responseObject =
                new ResponseObject(DATE_FORMAT_ERROR);

        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Date provided is either invalid, empty or in the incorrect format, please use the format of 'yyyy-mm-dd' e.g '2000-12-20'", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test if Request Parameter Error returned")
    void testRequestParamErrorReturned() {

        ResponseObject responseObject =
                new ResponseObject(REQUEST_PARAMETER_ERROR);

        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Invalid url parameter for search_type, please try 'alphabetical', 'best-match' or 'previous-name-dissolved'", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test if size parameter is invalid, greater than max size allowed")
    void testSizeParameterInvalidAlphabetical() {

        ResponseObject responseObject =
                new ResponseObject(SIZE_PARAMETER_ERROR);

        when(mockEnvironmentReader.getMandatoryInteger(MAX_SIZE_PARAM)).thenReturn(50);
        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        assertEquals("Invalid size parameter, size must be greater than zero and not greater than 50", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test if Internal Server Error returned")
    void testInternalServerErrorReturned() {

        ResponseObject responseObject =
            new ResponseObject(SEARCH_ERROR);

        ResponseEntity<?> responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}

