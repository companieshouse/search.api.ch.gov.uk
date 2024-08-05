package uk.gov.companieshouse.search.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;

import java.io.IOException;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@RestController
@RequestMapping(value = "/company-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanySearchController {

    private final ApiToResponseMapper apiToResponseMapper;

    private final PrimarySearchDeleteService primarySearchDeleteService;
    private final UpsertCompanyService upsertCompanyService;


    public CompanySearchController(ApiToResponseMapper apiToResponseMapper,
            PrimarySearchDeleteService primarySearchDeleteService, UpsertCompanyService upsertCompanyService) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.primarySearchDeleteService = primarySearchDeleteService;
        this.upsertCompanyService = upsertCompanyService;
    }

    @PutMapping("/companies/{company_number}")
    public ResponseEntity<Object> upsertCompanyPrimarySearch(@PathVariable("company_number") String companyNumber,
            @Valid @RequestBody Data profileData) {
        ResponseObject responseObject = upsertCompanyService.upsertCompany(companyNumber, profileData);
        return apiToResponseMapper.map(responseObject);
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
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ioException.getMessage());
            }
        }
        return apiToResponseMapper.map(responseObject);
    }
}