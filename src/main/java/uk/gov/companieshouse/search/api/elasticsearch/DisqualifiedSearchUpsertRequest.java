package uk.gov.companieshouse.search.api.elasticsearch;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.exception.UpsertException;

@Component
public class DisqualifiedSearchUpsertRequest {

    public String buildRequest(OfficerDisqualification officer) throws UpsertException, IOException {
        for (Item item : officer.getItems()) {
            if (ObjectUtils.isEmpty(item.getAddress())) {
                throw new UpsertException("Missing or empty mandatory Address field");
            }
        }
        checkMandatoryValues(officer.getKind(), officer.getLinks().getSelf());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(officer);
    }

    private void checkMandatoryValues(String... properties) throws UpsertException {
        for (String property : properties) {
            if (StringUtils.isEmpty(property)) {
                throw new UpsertException("Mandatory property blank or null");
            }
        }
    }
}


