package uk.gov.companieshouse.search.api.mapper;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;

@Component
public class ApiToResponseMapper {

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String MAX_SIZE_PARAM = "MAX_SIZE_PARAM";
    private static final String ADVANCED_SEARCH_MAX_SIZE = "ADVANCED_SEARCH_MAX_SIZE";

    public ResponseEntity<Object> map(ResponseObject responseObject) {

        switch(responseObject.getStatus()) {
            case SEARCH_FOUND:
            case DOCUMENT_UPSERTED:
            case DOCUMENT_DELETED:
                return ResponseEntity.status(OK).body(responseObject.getData());
            case SEARCH_NOT_FOUND:
            case DELETE_NOT_FOUND:
                return ResponseEntity.status(NOT_FOUND).build();
            case UPDATE_REQUEST_ERROR:
            case UPSERT_ERROR:
            case DELETE_REQUEST_ERROR:
                return ResponseEntity.status(BAD_REQUEST).build();
            case DATE_FORMAT_ERROR:
                return ResponseEntity.status(BAD_REQUEST)
                        .body("Date provided is either invalid, empty or in the incorrect format, " +
                            "please use the format of 'yyyy-mm-dd' e.g '2000-12-20'");
            case MAPPING_ERROR:
                return ResponseEntity.status(BAD_REQUEST)
                    .body("Error attempting to map request parameter values, please check the values of fields " +
                        "'company_status' or 'company_type' or 'company_subtype' contain accurate values");
            case REQUEST_PARAMETER_ERROR:
                return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Invalid url parameter for search_type, " +
                            "please try 'alphabetical', 'best-match' or 'previous-name-dissolved'");
            case SIZE_PARAMETER_ERROR:
                return ResponseEntity.status(UNPROCESSABLE_ENTITY)
                    .body("Invalid size parameter, size must be greater than zero and not greater than "
                        + environmentReader.getMandatoryInteger(MAX_SIZE_PARAM));
            case SERVICE_UNAVAILABLE:
                    return ResponseEntity.status(SERVICE_UNAVAILABLE)
                        .body("API attempted to call an unavailable service");
            case ADVANCED_SIZE_PARAMETER_ERROR:
                return ResponseEntity.status(UNPROCESSABLE_ENTITY)
                        .body("Invalid size parameter, size must be greater than zero and not greater than "
                                + environmentReader.getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE));
            default:
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }
}
