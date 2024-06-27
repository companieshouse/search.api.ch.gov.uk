package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class CompanySearchLinks {
    @JsonProperty("self")
    private final String self;

    public CompanySearchLinks(String self) {
        this.self = self;
    }

    public String getSelf() {
        return self;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanySearchLinks that = (CompanySearchLinks) o;
        return Objects.equals(self, that.self);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self);
    }
}
