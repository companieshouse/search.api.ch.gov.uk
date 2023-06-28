package uk.gov.companieshouse.search.api.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchDocument;

@Component
public class OfficersSearchUpsertRequest {


    private static final String KIND = "searchresults#officer";

    public String buildRequest(OfficerSearchDocument officerSearchDocument) throws IOException {
        for (OfficerSearchAppointment item : officerSearchDocument.getItems()) {
            if (ObjectUtils.isEmpty(item.getAddress())) {
                throw new UpsertException("Missing or empty mandatory Address field");
            }
        }
        checkMandatoryValues(officerSearchDocument.getLinks().getSelf(), officerSearchDocument.getKind());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(officerSearchDocument);
    }

    private void checkMandatoryValues(String self, String kind) throws UpsertException {
        if (StringUtils.isEmpty(self) || ! (self.contains("officers") || self.contains("appointments"))) {
            throw new UpsertException("self link in incorrect format");
        }
        if (StringUtils.isEmpty(kind) || ! kind.equals(KIND)) {
            throw new UpsertException("Kind was not for officers");
        }
    }

}
