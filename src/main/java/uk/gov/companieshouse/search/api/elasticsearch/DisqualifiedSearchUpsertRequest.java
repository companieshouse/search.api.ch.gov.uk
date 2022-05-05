package uk.gov.companieshouse.search.api.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.disqualification.OfficerDisqualification;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DisqualifiedSearchUpsertRequest {

    public String buildRequest(OfficerDisqualification officer) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if (localDate == null) {
                    jsonGenerator.writeNull();
                } else {
                    DateTimeFormatter dateTimeFormatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String format = localDate.atStartOfDay().format(dateTimeFormatter);
                    jsonGenerator.writeRawValue("\"" + format + "\"");
                }
            }
        });
        mapper.registerModule(module);
        return mapper.writeValueAsString(officer);
    }
}


