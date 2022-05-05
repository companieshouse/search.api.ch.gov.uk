package uk.gov.companieshouse.search.api.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;

import java.io.IOException;

@Component
public class DisqualifiedSearchUpsertRequest {

    public String buildRequest(OfficerDisqualification officer) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(officer);
    }
}


