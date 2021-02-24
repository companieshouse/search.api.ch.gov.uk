package uk.gov.companieshouse.search.api.model.esdatamodel.dissolved;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"ordered_alpha_key"})
public class PreviousCompanyName {

    @JsonProperty("ceased_on")
    private String dateOfNameCessation;

    @JsonProperty("effective_from")
    private String dateOfNameEffectiveness;

    @JsonProperty("name")
    private String name;

    public String getDateOfNameCessation() {
        return dateOfNameCessation;
    }

    public void setDateOfNameCessation(String dateOfNameCessation) {
        this.dateOfNameCessation = dateOfNameCessation;
    }

    public String getDateOfNameEffectiveness() {
        return dateOfNameEffectiveness;
    }

    public void setDateOfNameEffectiveness(String dateOfNameEffectiveness) {
        this.dateOfNameEffectiveness = dateOfNameEffectiveness;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
