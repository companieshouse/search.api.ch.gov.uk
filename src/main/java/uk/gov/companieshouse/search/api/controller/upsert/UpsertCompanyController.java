package uk.gov.companieshouse.search.api.controller.upsert;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NAME;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_NUMBER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.UPSERT_COMPANY_NUMBER;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.getLogger;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

@RestController
@RequestMapping(value = "/alphabetical-search/companies/{company_number}")
public class UpsertCompanyController {

    @Autowired
    private UpsertCompanyService upsertCompanyService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @PutMapping
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
