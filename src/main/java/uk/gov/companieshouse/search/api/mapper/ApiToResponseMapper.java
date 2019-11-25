package uk.gov.companieshouse.search.api.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;

@Component
public class ApiToResponseMapper {

    public ResponseEntity map(ResponseObject responseObject) {

        switch(responseObject.getStatus()) {
            case SEARCH_FOUND:
                return ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
            case SEARCH_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
