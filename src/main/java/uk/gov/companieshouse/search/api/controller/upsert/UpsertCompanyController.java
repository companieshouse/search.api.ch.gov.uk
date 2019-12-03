package uk.gov.companieshouse.search.api.controller.upsert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.upsert.UpsertCompanyService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(value = "/upsert-company")
public class UpsertCompanyController {

    @Autowired
    private UpsertCompanyService upsertCompanyService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @PostMapping
    public ResponseEntity upsertCompany(@Valid @RequestBody Company company) {

        ResponseObject responseObject = null;
        try {
            responseObject = upsertCompanyService.upsert(company);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return apiToResponseMapper.map(responseObject);
    }
}
