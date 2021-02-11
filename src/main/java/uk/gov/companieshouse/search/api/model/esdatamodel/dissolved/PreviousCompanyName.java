package uk.gov.companieshouse.search.api.model.esdatamodel.dissolved;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreviousCompanyName {

    @JsonProperty("date_of_name_cessation")
    private String dateOfNameCessation;

    @JsonProperty("date_of_name_effectiveness")
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
