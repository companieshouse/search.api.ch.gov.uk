package uk.gov.companieshouse.search.api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.mapper.AdvancedQueryParamMapper;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.delete.advanced.AdvancedSearchDeleteService;
import uk.gov.companieshouse.search.api.service.delete.primary.PrimarySearchDeleteService;
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchIndexService;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

@RestController
@RequestMapping(value = "/primary-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrimarySearchController {

    private final ApiToResponseMapper apiToResponseMapper;

    private final PrimarySearchDeleteService primarySearchDeleteService;

    public PrimarySearchController(ApiToResponseMapper apiToResponseMapper,
                                   PrimarySearchDeleteService primarySearchDeleteService) {
        this.apiToResponseMapper = apiToResponseMapper;
        this.primarySearchDeleteService = primarySearchDeleteService;
    }

    @DeleteMapping("/companies/{company_number}")
    public ResponseEntity<Object> deletePrimarySearch(@PathVariable("company_number")
                                                          String companyNumber) throws IOException {

        ResponseObject responseObject;
        getLogger().info(String
                .format("Attempting to delete company number [%s] from the advanced search index",
                companyNumber));

        if (companyNumber == null || companyNumber.isEmpty()){
            responseObject = new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
        }
        else {
            responseObject = primarySearchDeleteService.deleteCompanyByNumber(companyNumber);
        }
        return apiToResponseMapper.map(responseObject);
    }
}