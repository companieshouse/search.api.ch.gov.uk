package uk.gov.companieshouse.search.api.model.esdatamodel;

import java.util.Objects;

public class OfficerSearchLinks {

    private final String self;

    public OfficerSearchLinks(String self) {
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
        OfficerSearchLinks that = (OfficerSearchLinks) o;
        return Objects.equals(self, that.self);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self);
    }
}
