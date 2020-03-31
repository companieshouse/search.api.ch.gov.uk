package uk.gov.companieshouse.search.api.controller.upsert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/upsert-company")
public class UpsertCompanyController {

    @Autowired
    private UpsertCompanyService upsertCompanyService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @PutMapping
    public ResponseEntity upsertCompany(@Valid @RequestBody CompanyProfileApi company) {

        ResponseObject responseObject = upsertCompanyService.upsert(company);

        return apiToResponseMapper.map(responseObject);
    }
}
