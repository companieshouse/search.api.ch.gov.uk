package uk.gov.companieshouse.search.api.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.request.AlphabeticalSearchRequest;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/alphabetical-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlphabeticalSearchController {

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";

    @Autowired
    private SearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity searchByCorporateName(@RequestParam(name = "q") String companyName,
                                                @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        ResponseObject responseObject = searchIndexService
            .search(companyName, requestId);

        return apiToResponseMapper.map(responseObject);
    }
}
