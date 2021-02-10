package uk.gov.companieshouse.search.api.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.search.api.mapper.ApiToResponseMapper;
import uk.gov.companieshouse.search.api.model.request.AlphabeticalSearchRequest;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.search.SearchIndexService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/alphabetical-search", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlphabeticalSearchController {

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";

    @Autowired
    private SearchIndexService searchIndexService;

    @Autowired
    private ApiToResponseMapper apiToResponseMapper;

    @PostMapping("/corporate-name")
    @ResponseBody
    public ResponseEntity searchByCorporateName(@Valid @RequestBody
                                                AlphabeticalSearchRequest alphabeticalSearchRequest,
                                                @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        ResponseObject responseObject = searchIndexService
            .search(alphabeticalSearchRequest.getCompanyName(), requestId);

        return apiToResponseMapper.map(responseObject);
    }
}
