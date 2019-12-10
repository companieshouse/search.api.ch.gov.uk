package uk.gov.companieshouse.search.api.model.esdatamodel.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company implements Comparable<Company> {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("items")
    private Items items;

    @JsonProperty("links")
    private Links links;

    public Company() {
    }

    public Company(String id, String companyType,
        Items items, Links links) {
        this.id = id;
        this.companyType = companyType;
        this.items = items;
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;
        Company company = (Company) o;
        return Objects.equals(getId(), company.getId()) &&
            Objects.equals(getCompanyType(), company.getCompanyType()) &&
            Objects.equals(getItems(), company.getItems()) &&
            Objects.equals(getLinks(), company.getLinks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCompanyType(), getItems(), getLinks());
    }

    @Override
    public int compareTo(Company argCompany) {

        // compare corporate names removing whitespace and punctuation
        String regexPattern = "[^A-Za-z]+";
        String replacement = "";

        String a = items.getCorporateName().replace(regexPattern, replacement);
        String b = argCompany.getItems().getCorporateName().replace(regexPattern, replacement);

        return a.compareTo(b);
    }
}
