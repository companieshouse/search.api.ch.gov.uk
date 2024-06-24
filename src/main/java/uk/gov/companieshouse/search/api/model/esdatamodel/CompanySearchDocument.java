package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public class CompanySearchDocument {

    public static final String RESOURCE_KIND = "searchresults#company";

    @JsonProperty("items")
    private List<CompanySearchData> items;

    @JsonProperty("company_type")
    private final String companyType;

    @JsonProperty("kind")
    private final String kind;

    @JsonProperty("links")
    private final CompanySearchLinks links;

    @JsonProperty("sort_key")
    private final String sortKey;

    private CompanySearchDocument(Builder builder) {
        items = builder.items;
        companyType = builder.companyType;
        kind = builder.kind;
        links = builder.links;
        sortKey = builder.sortKey;
    }

    public List<CompanySearchData> getItems() {
        return items;
    }

    public String getCompanyType() {
        return companyType;
    }

    public String getKind() {
        return kind;
    }

    public CompanySearchLinks getLinks() {
        return links;
    }

    public String getSortKey() {
        return sortKey;
    }
    public static final class Builder {

        private List<CompanySearchData> items;

        private String companyType;

        private String kind;

        private CompanySearchLinks links;

        private String sortKey;
        public static Builder builder() {
            return new Builder();
        }

        private Builder() {
            this.kind = RESOURCE_KIND;
        }

        public Builder items(List<CompanySearchData> items) {
            this.items = items;
            return this;
        }

        public Builder companyType(String companyType) {
            this.companyType = companyType;
            return this;
        }

        public Builder kind(String kind) {
            this.kind = kind;
            return this;
        }

        public Builder links(CompanySearchLinks links) {
            this.links = links;
            return this;
        }

        public Builder sortKey(String sortKey) {
            this.sortKey = sortKey;
            return this;
        }

        public CompanySearchDocument build() {
            return new CompanySearchDocument(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanySearchDocument that = (CompanySearchDocument) o;
        return Objects.equals(items, that.items) && Objects.equals(companyType, that.companyType)
                && Objects.equals(kind, that.kind) && Objects.equals(links, that.links)
                && Objects.equals(sortKey, that.sortKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, companyType, kind, links, sortKey);
    }
}