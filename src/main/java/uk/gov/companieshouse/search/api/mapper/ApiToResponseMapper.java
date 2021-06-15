package uk.gov.companieshouse.search.api.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
public class ApiToResponseMapper {

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String MAX_SIZE_PARAM = "MAX_SIZE_PARAM";

    public ResponseEntity<Object> map(ResponseObject responseObject) {

        switch(responseObject.getStatus()) {
            case SEARCH_FOUND:
            case DOCUMENT_UPSERTED:
                return ResponseEntity.status(OK).body(responseObject.getData());
            case SEARCH_NOT_FOUND:
                return ResponseEntity.status(NOT_FOUND).build();
            case UPDATE_REQUEST_ERROR:
            case UPSERT_ERROR:
                return ResponseEntity.status(BAD_REQUEST).build();
            case SIZE_PARAMETER_ERROR:
                return ResponseEntity.status(UNPROCESSABLE_ENTITY)
                    .body("Invalid size parameter, size must be greater than zero and not greater than " + environmentReader.getMandatoryInteger(MAX_SIZE_PARAM));
            default:
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    // To be removed when alphabetical search response has been updated
    public ResponseEntity<Object> mapDissolved(ResponseObject responseObject) {

        switch(responseObject.getStatus()) {
            case SEARCH_FOUND:
                return ResponseEntity.status(OK).body(responseObject.getData());
            case SEARCH_NOT_FOUND:
                return ResponseEntity.status(NOT_FOUND).build();
            case REQUEST_PARAMETER_ERROR:
                return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Invalid url parameter for search_type, please try 'alphabetical', 'best-match' or 'previous-name-dissolved'");
            case SIZE_PARAMETER_ERROR:
                return ResponseEntity.status(UNPROCESSABLE_ENTITY)
                    .body("Invalid size parameter, size must be greater than zero and and not greater than " + environmentReader.getMandatoryInteger(MAX_SIZE_PARAM));
            default:
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }
}
