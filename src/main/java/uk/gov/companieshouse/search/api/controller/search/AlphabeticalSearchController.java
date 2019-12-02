package uk.gov.companieshouse.search.api.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.SearchIndexService;

@Controller
@RequestMapping(value = "/alphabeticalSearch")
public class AlphabeticalSearchController {

    @Autowired
    private SearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;


    @GetMapping("/corporateName")
    public ResponseEntity searchByCorporateName(@Valid @RequestBody) {

        ResponseObject responseObject = searchIndexService.search(corporateName);

        return apiToResponseMapper.map(responseObject);
    }
}
