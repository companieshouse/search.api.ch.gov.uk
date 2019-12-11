package uk.gov.companieshouse.search.api.mapper;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Component
public class ApiToResponseMapper {

    public ResponseEntity map(ResponseObject responseObject) {

        switch(responseObject.getStatus()) {
            case SEARCH_FOUND:
                return ResponseEntity.status(FOUND).body(responseObject.getData());
            case SEARCH_NOT_FOUND:
                return ResponseEntity.status(NOT_FOUND).build();
            case DOCUMENT_UPSERTED:
                return ResponseEntity.status(OK).build();
            case UPDATE_REQUEST_ERROR:
            case UPSERT_ERROR:
                return ResponseEntity.status(BAD_REQUEST).build();
            default:
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }
}
