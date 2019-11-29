package uk.gov.companieshouse.search.api.model.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {

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
}
