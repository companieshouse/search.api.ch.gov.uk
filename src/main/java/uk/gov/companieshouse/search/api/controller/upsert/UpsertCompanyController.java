package uk.gov.companieshouse.search.api.controller.upsert;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.logging.LoggingUtils;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

@RestController
@RequestMapping(value = "/upsert-company")
public class UpsertCompanyController {

    @Autowired
    private UpsertCompanyService upsertCompanyService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @PutMapping
    public ResponseEntity upsertCompany(@Valid @RequestBody CompanyProfileApi company) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LoggingUtils.COMPANY_NAME, company.getCompanyName());
        logMap.put(LoggingUtils.COMPANY_NUMBER, company.getCompanyNumber());
        LoggingUtils.getLogger().info("Upserting company", logMap);

        DissolvedResponseObject responseObject = upsertCompanyService.upsert(company);

        return apiToResponseMapper.map(responseObject);
    }
}
