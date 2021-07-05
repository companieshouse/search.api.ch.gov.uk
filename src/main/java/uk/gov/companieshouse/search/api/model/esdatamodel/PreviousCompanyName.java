package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"ordered_alpha_key"})
public class PreviousCompanyName {

    @JsonProperty("ceased_on")
    private LocalDate dateOfNameCessation;

    @JsonProperty("effective_from")
    private LocalDate dateOfNameEffectiveness;

    @JsonProperty("name")
    private String name;
    
    @JsonProperty("company_number")
    private String companyNumber;

    public LocalDate getDateOfNameCessation() {
        return dateOfNameCessation;
    }

    public void setDateOfNameCessation(LocalDate dateOfNameCessation) {
        this.dateOfNameCessation = dateOfNameCessation;
    }

    public LocalDate getDateOfNameEffectiveness() {
        return dateOfNameEffectiveness;
    }

    public void setDateOfNameEffectiveness(LocalDate dateOfNameEffectiveness) {
        this.dateOfNameEffectiveness = dateOfNameEffectiveness;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }
}
