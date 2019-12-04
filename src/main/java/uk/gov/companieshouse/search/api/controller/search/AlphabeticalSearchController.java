package uk.gov.companieshouse.search.api.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.request.AlphabeticalSearchRequest;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/alphabeticalSearch", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlphabeticalSearchController {

    @Autowired
    private SearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;


    @PostMapping("/corporateName")
    @ResponseBody
    public ResponseEntity searchByCorporateName(@Valid @RequestBody
        AlphabeticalSearchRequest alphabeticalSearchRequest) {

        ResponseObject responseObject = searchIndexService
            .search(alphabeticalSearchRequest.getCompanyName());

        return apiToResponseMapper.map(responseObject);
    }
}
