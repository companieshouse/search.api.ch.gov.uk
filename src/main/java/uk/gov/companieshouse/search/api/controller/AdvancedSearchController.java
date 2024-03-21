package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchIndexService;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@RestController
@RequestMapping(value = "/advanced-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvancedSearchController {

    private static final String START_INDEX_QUERY_PARAM = "start_index";
    private static final String COMPANY_NAME_QUERY_PARAM = "company_name_includes";
    private static final String LOCATION_QUERY_PARAM = "location";
    private static final String INCORPORATED_FROM_QUERY_PARAMETER = "incorporated_from";
    private static final String INCORPORATED_TO_QUERY_PARAMETER = "incorporated_to";
    private static final String COMPANY_STATUS_QUERY_PARAMETER = "company_status";
    private static final String SIC_CODE_QUERY_PARAMETER = "sic_codes";
    private static final String COMPANY_TYPE_QUERY_PARAMETER = "company_type";
    private static final String COMPANY_SUBTYPE_QUERY_PARAMETER = "company_subtype";
    private static final String DISSOLVED_FROM_QUERY_PARAMETER = "dissolved_from";
    private static final String DISSOLVED_TO_QUERY_PARAMETER = "dissolved_to";
    private static final String COMPANY_NAME_EXCLUDES = "company_name_excludes";
    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String SIZE_PARAM = "size";

    private final AdvancedQueryParamMapper queryParamMapper;
    private final AdvancedSearchIndexService searchIndexService;
    private final ApiToResponseMapper apiToResponseMapper;
    private final UpsertCompanyService upsertCompanyService;
    private final ConfiguredIndexNamesProvider indices;
    private final AdvancedSearchDeleteService advancedSearchDeleteService;

    public AdvancedSearchController(AdvancedQueryParamMapper queryParamMapper,
        AdvancedSearchIndexService searchIndexService, ApiToResponseMapper apiToResponseMapper,
        UpsertCompanyService upsertCompanyService, ConfiguredIndexNamesProvider indices,
                                    AdvancedSearchDeleteService advancedSearchDeleteService) {
        this.queryParamMapper = queryParamMapper;
        this.searchIndexService = searchIndexService;
        this.apiToResponseMapper = apiToResponseMapper;
        this.upsertCompanyService = upsertCompanyService;
        this.advancedSearchDeleteService = advancedSearchDeleteService;
        this.indices = indices;
    }

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<Object> search(@RequestParam(name = START_INDEX_QUERY_PARAM, required = false) Integer startIndex,
                                         @RequestParam(name = COMPANY_NAME_QUERY_PARAM, required = false) String companyName,
                                         @RequestParam(name = LOCATION_QUERY_PARAM, required = false) String location,
                                         @RequestParam(name = INCORPORATED_FROM_QUERY_PARAMETER, required = false) String incorporatedFrom,
                                         @RequestParam(name = INCORPORATED_TO_QUERY_PARAMETER, required = false) String incorporatedTo,
                                         @RequestParam(name = COMPANY_STATUS_QUERY_PARAMETER, required = false) List<String> companyStatusList,
                                         @RequestParam(name = SIC_CODE_QUERY_PARAMETER, required = false) List<String> sicCodes,
                                         @RequestParam(name = COMPANY_TYPE_QUERY_PARAMETER, required = false) List<String> companyTypeList,
                                         @RequestParam(name = COMPANY_SUBTYPE_QUERY_PARAMETER, required = false) List<String> companySubtypeList,
                                         @RequestParam(name = DISSOLVED_FROM_QUERY_PARAMETER, required = false) String dissolvedFrom,
                                         @RequestParam(name = DISSOLVED_TO_QUERY_PARAMETER, required = false) String dissolvedTo,
                                         @RequestParam(name = COMPANY_NAME_EXCLUDES, required = false) String companyNameExcludes,
                                         @RequestParam(name = SIZE_PARAM, required = false) Integer size,
                                         @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date incorporatedFromDate = null;
        Date incorporatedToDate = null;
        Date dissolvedFromDate = null;
        Date dissolvedToDate = null;
        try {
            if(incorporatedFrom != null){
                incorporatedFromDate = formatter.parse(incorporatedFrom);
            }
            if(incorporatedTo != null){
                incorporatedToDate = formatter.parse(incorporatedTo);
            }
            if(dissolvedFrom != null){
                dissolvedFromDate = formatter.parse(dissolvedFrom);
            }
            if(dissolvedTo != null){
                dissolvedToDate = formatter.parse(dissolvedTo);
            }
        } catch (ParseException e) {
            getLogger().error("Date passed in wrong format to advanced search controller", e);
        }


        Map<String, Object> logMap = new DataMap.Builder()
                .companyName(companyName)
                .startIndex(String.valueOf(startIndex))
                .location(location)
                .incorporatedFrom(incorporatedFromDate)
                .incorporatedTo(incorporatedToDate)
                .companyStatus(companyStatusList)
                .sicCodes(sicCodes)
                .companyType(companyTypeList)
                .companySubtype(companySubtypeList)
                .dissolvedFrom(dissolvedFromDate)
                .dissolvedTo(dissolvedToDate)
                .companyNameExcludes(companyNameExcludes)
                .size(String.valueOf(size))
                .indexName(indices.advanced())
                .build().getLogMap();

        getLogger().info("Search request received", logMap);
        logMap.remove(MESSAGE);

        AdvancedSearchQueryParams advancedSearchQueryParams;

        try {
            advancedSearchQueryParams = queryParamMapper
                .mapAdvancedQueryParameters(startIndex, companyName, location, incorporatedFrom,
                    incorporatedTo, companyStatusList, sicCodes, companyTypeList, companySubtypeList, dissolvedFrom, dissolvedTo, companyNameExcludes, size);
        } catch (DateFormatException dfe) {
           return apiToResponseMapper.map(new ResponseObject(ResponseStatus.DATE_FORMAT_ERROR, null));
        } catch (MappingException me) {
            return apiToResponseMapper.map(new ResponseObject(ResponseStatus.MAPPING_ERROR, null));
        } catch (SizeException se) {
            return apiToResponseMapper.map(new ResponseObject(ResponseStatus.ADVANCED_SIZE_PARAMETER_ERROR, null));
        }

        ResponseObject responseObject = searchIndexService.searchAdvanced(advancedSearchQueryParams, requestId);

        return apiToResponseMapper.map(responseObject);
    }

    @PutMapping("/companies/{company_number}")
    public ResponseEntity<Object> upsert(@PathVariable("company_number") String companyNumber,
                                         @Valid @RequestBody CompanyProfileApi company) {

        Map<String, Object> logMap = new DataMap.Builder()
                .companyName(company.getCompanyName())
                .companyNumber(company.getCompanyNumber())
                .upsertCompanyNumber(companyNumber)
                .build().getLogMap();

        getLogger().info("Attempting to upsert a company to advanced search index", logMap);

        ResponseObject responseObject;

        if (companyNumber == null || companyNumber.isEmpty()
            || !companyNumber.equalsIgnoreCase(company.getCompanyNumber())) {
            responseObject = new ResponseObject(ResponseStatus.UPSERT_ERROR);
        } else {
            responseObject = upsertCompanyService.upsertAdvanced(company);
        }
        return apiToResponseMapper.map(responseObject);
    }

    @DeleteMapping("/companies/{company_number}")
    public ResponseEntity<Object> deleteCompany(@PathVariable("company_number") String companyNumber) {

        getLogger().info("Attempting to delete company number from advanced search index");
        ResponseObject responseObject;

        if (companyNumber == null || companyNumber.isEmpty()){
            responseObject = new ResponseObject(ResponseStatus.DELETE_NOT_FOUND);
        }
        else {
            responseObject = advancedSearchDeleteService.deleteCompanyByNumber(companyNumber);
        }
        return apiToResponseMapper.map(responseObject);
    }
}