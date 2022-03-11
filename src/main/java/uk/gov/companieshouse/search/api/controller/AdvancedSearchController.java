package uk.gov.companieshouse.search.api.controller;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NUMBER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_STATUS;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_SUBTYPE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_TYPE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISSOLVED_FROM;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.DISSOLVED_TO;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INCORPORATED_FROM;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INCORPORATED_TO;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.LOCATION;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.MESSAGE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SIC_CODES;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.START_INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.UPSERT_COMPANY_NUMBER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.AdvancedQueryParamMapper;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.impl.advanced.AdvancedSearchIndexService;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

@RestController
@RequestMapping(value = "/advanced-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvancedSearchController {

    @Autowired
    private AdvancedQueryParamMapper queryParamMapper;

    @Autowired
    private AdvancedSearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @Autowired
    private UpsertCompanyService upsertCompanyService;

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

        Map<String, Object> logMap = LoggingUtils.createLoggingMap(requestId);
        logIfNotNull(logMap, START_INDEX, startIndex);
        logIfNotNull(logMap, COMPANY_NAME, companyName);
        logIfNotNull(logMap, LOCATION, location);
        logIfNotNull(logMap, INCORPORATED_FROM, incorporatedFrom);
        logIfNotNull(logMap, INCORPORATED_TO, incorporatedTo);
        logIfNotNull(logMap, COMPANY_STATUS, companyStatusList);
        logIfNotNull(logMap, SIC_CODES, sicCodes);
        logIfNotNull(logMap, COMPANY_TYPE, companyTypeList);
        logIfNotNull(logMap, COMPANY_SUBTYPE, companySubtypeList);
        logIfNotNull(logMap, DISSOLVED_FROM, dissolvedFrom);
        logIfNotNull(logMap, DISSOLVED_TO, dissolvedTo);
        logIfNotNull(logMap, COMPANY_NAME_EXCLUDES, companyNameExcludes);
        logIfNotNull(logMap, SIZE_PARAM, size);
        logMap.put(INDEX, LoggingUtils.ADVANCED_SEARCH_INDEX);
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

        Map<String, Object> logMap = new HashMap<>();
        logMap.put(COMPANY_NAME, company.getCompanyName());
        logMap.put(COMPANY_NUMBER, company.getCompanyNumber());
        logMap.put(UPSERT_COMPANY_NUMBER, companyNumber);
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
}