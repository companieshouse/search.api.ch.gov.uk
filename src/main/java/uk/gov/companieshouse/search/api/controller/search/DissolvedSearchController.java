package uk.gov.companieshouse.search.api.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.response.DissolvedResponseObject;
import uk.gov.companieshouse.search.api.service.search.impl.dissolved.DissolvedSearchIndexService;

@RestController
@RequestMapping(value = "/dissolved-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class DissolvedSearchController {

    @Autowired
    private DissolvedSearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String COMPANY_NAME_QUERY_PARAM = "q";

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity searchCompanies(@RequestParam(name = COMPANY_NAME_QUERY_PARAM) String companyName,
                                                @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        DissolvedResponseObject responseObject = searchIndexService
                .search(companyName, requestId);

        return apiToResponseMapper.mapDissolved(responseObject);
    }
}
