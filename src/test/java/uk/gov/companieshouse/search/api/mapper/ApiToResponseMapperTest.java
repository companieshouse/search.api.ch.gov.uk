package uk.gov.companieshouse.search.api.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.search.api.model.SearchResults;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiToResponseMapperTest {

    @InjectMocks
    private ApiToResponseMapper apiToResponseMapper;

    @Test
    @DisplayName("Test if Found returned")
    public void testFoundReturned() {

        ResponseObject responseObject =
            new ResponseObject(ResponseStatus.SEARCH_FOUND, new SearchResults());

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Not Found returned")
    public void testNotFoundReturned() {

        ResponseObject responseObject =
            new ResponseObject(ResponseStatus.SEARCH_NOT_FOUND);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test if Internal Server Error returned")
    public void testInternalServerErrorReturned() {

        ResponseObject responseObject =
            new ResponseObject(ResponseStatus.SEARCH_ERROR);

        ResponseEntity responseEntity = apiToResponseMapper.map(responseObject);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

}