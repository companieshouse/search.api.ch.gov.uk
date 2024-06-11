package uk.gov.companieshouse.search.api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import java.io.IOException;
import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@RestController
@RequestMapping(value = "/primary-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyPrimarySearchController {

    private final ApiToResponseMapper apiToResponseMapper;

    private final PrimarySearchDeleteService primarySearchDeleteService;

    private final ConfiguredIndexNamesProvider indices;

    public CompanyPrimarySearchController(ApiToResponseMapper apiToResponseMapper,
                                          PrimarySearchDeleteService primarySearchDeleteService,
                                          ConfiguredIndexNamesProvider indices) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.primarySearchDeleteService = primarySearchDeleteService;
        this.indices = indices;
    }

    @DeleteMapping("/companies/{company_number}")
    public ResponseEntity<Object> deleteCompanyPrimarySearch(@PathVariable("company_number")
                                                          String companyNumber) throws IOException {
        ResponseObject responseObject;
        getLogger().info(String
                .format("Attempting to delete company number [%s] from the advanced search index",
                companyNumber));

        if (companyNumber == null || companyNumber.isEmpty()){
            responseObject = new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
            getLogger().error(String.format("company [%s] not found",
                    companyNumber));
        }
        else {
            responseObject = primarySearchDeleteService.deleteCompanyByNumber(companyNumber);
            getLogger().info(String.format("Successfully deleted company [%s] ",
                    companyNumber));
        }
        return apiToResponseMapper.map(responseObject);
    }
}