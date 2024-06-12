package uk.gov.companieshouse.search.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@RestController
@RequestMapping(value = "/primary-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyPrimarySearchController {

    private final ApiToResponseMapper apiToResponseMapper;

    private final PrimarySearchDeleteService primarySearchDeleteService;


    public CompanyPrimarySearchController(ApiToResponseMapper apiToResponseMapper,
                                          PrimarySearchDeleteService primarySearchDeleteService) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.primarySearchDeleteService = primarySearchDeleteService;
    }

    @DeleteMapping("/companies/{company_number}")
    public ResponseEntity<Object> deleteCompanyPrimarySearch(@PathVariable("company_number")
                                                          String companyNumber) throws IOException {
        ResponseObject responseObject;
        getLogger().info(String
                .format("Attempting to delete company number [%s] from the primary search index",
                companyNumber));

        if (companyNumber == null || companyNumber.isEmpty()){
            responseObject = new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
            getLogger().error(String.format("company [%s] not found",
                    companyNumber));
        }
        else {
            try{
                responseObject = primarySearchDeleteService.deleteCompanyByNumber(companyNumber);
                getLogger().info(String.format("Successfully deleted company [%s] ",
                        companyNumber));
            } catch (IOException ioException) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(ioException.getMessage());
            }
        }
        return apiToResponseMapper.map(responseObject);
    }
}