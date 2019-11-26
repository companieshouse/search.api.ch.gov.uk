package uk.gov.companieshouse.search.api.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.service.SearchIndexService;

@Service
public class AlphabeticalSearchIndexServiceImpl implements SearchIndexService {

    @Override
    public ResponseObject search(String searchParam) {
        return null;
    }
}
