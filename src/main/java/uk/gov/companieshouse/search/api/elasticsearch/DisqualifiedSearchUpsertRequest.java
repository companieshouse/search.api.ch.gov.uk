package uk.gov.companieshouse.search.api.elasticsearch;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.disqualification.Item;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;
import uk.gov.companieshouse.search.api.exception.UpsertException;

@Component
public class DisqualifiedSearchUpsertRequest {
 
    private static final String KIND = "searchresults#disqualified-officer";

    public String buildRequest(OfficerDisqualification officer) throws IOException {
        for (Item item : officer.getItems()) {
            if (ObjectUtils.isEmpty(item.getAddress())) {
                throw new UpsertException("Missing or empty mandatory Address field");
            }
        }
        checkMandatoryValues(officer.getLinks().getSelf(), officer.getKind());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(officer);
    }

    private void checkMandatoryValues(String self, String kind) throws UpsertException {
        if (StringUtils.isEmpty(self) || ! (self.contains("corporate") || self.contains("natural"))) {
            throw new UpsertException("self in incorrect format");
        }
        if (StringUtils.isEmpty(kind) || ! kind.equals(KIND)) {
            throw new UpsertException("Kind was not for a disqualified officer");
        }
    }
}
