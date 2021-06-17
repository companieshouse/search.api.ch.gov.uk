package uk.gov.companieshouse.search.api.controller.search;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NUMBER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.INDEX_ALPHABETICAL;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_AFTER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SEARCH_BEFORE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.SIZE;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.UPSERT_COMPANY_NUMBER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.createLoggingMap;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.logIfNotNull;

import java.util.HashMap;
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

import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;
import uk.gov.companieshouse.search.api.service.search.impl.alphabetical.AlphabeticalSearchIndexService;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;


@RestController
@RequestMapping(value = "/alphabetical-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlphabeticalSearchController {

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String COMPANY_NAME_QUERY_PARAM = "q";
    private static final String SEARCH_BEFORE_PARAM = "search_before";
    private static final String SEARCH_AFTER_PARAM = "search_after";
    private static final String SIZE_PARAM = "size";

    @Autowired
    private AlphabeticalSearchIndexService searchIndexService;
    
    @Autowired
    private UpsertCompanyService upsertCompanyService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String MAX_SIZE_PARAM = "MAX_SIZE_PARAM";
    private static final String ALPHABETICAL_SEARCH_RESULT_MAX = "ALPHABETICAL_SEARCH_RESULT_MAX";

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<?> searchByCorporateName(@RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
                                                   @RequestParam(name = SEARCH_BEFORE_PARAM, required = false) String searchBefore,
                                                   @RequestParam(name = SEARCH_AFTER_PARAM, required = false) String searchAfter,
                                                   @RequestParam(name = SIZE_PARAM, required = false) Integer size,
                                                   @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        Map<String, Object> logMap = createLoggingMap(requestId);
        logMap.put(COMPANY_NAME, companyName);
        logMap.put(INDEX, INDEX_ALPHABETICAL);
        logIfNotNull(logMap, SEARCH_BEFORE, searchBefore);
        logIfNotNull(logMap, SEARCH_AFTER, searchAfter);
        logIfNotNull(logMap, SIZE, size);
        getLogger().info("Search request received", logMap);

        try {
            size = SearchRequestUtils.checkResultsSize
                (size, environmentReader.getMandatoryInteger(ALPHABETICAL_SEARCH_RESULT_MAX),
                    environmentReader.getMandatoryInteger(MAX_SIZE_PARAM));
        } catch (SizeException e) {
            getLogger().info(e.getMessage(), logMap);
            return apiToResponseMapper
                .map(new ResponseObject(ResponseStatus.SIZE_PARAMETER_ERROR, null));
        }

        ResponseObject responseObject = searchIndexService
            .search(companyName, searchBefore, searchAfter, size, requestId);

        return apiToResponseMapper.map(responseObject);
    }
    
    @PutMapping("/companies/{company_number}")
    public ResponseEntity<Object> upsertCompany(@PathVariable("company_number") String companyNumber,
            @Valid @RequestBody CompanyProfileApi company) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(COMPANY_NAME, company.getCompanyName());
        logMap.put(COMPANY_NUMBER, company.getCompanyNumber());
        logMap.put(UPSERT_COMPANY_NUMBER, companyNumber);
        getLogger().info("Upserting company", logMap);

        ResponseObject responseObject;

        if (companyNumber == null || companyNumber.isEmpty()
                || !companyNumber.equalsIgnoreCase(company.getCompanyNumber())) {
            responseObject = new ResponseObject(ResponseStatus.UPSERT_ERROR);
        } else {
            responseObject = upsertCompanyService.upsert(company);
        }
        return apiToResponseMapper.map(responseObject);
    }
}
