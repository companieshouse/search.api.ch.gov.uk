package uk.gov.companieshouse.search.api.model.esdatamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import uk.gov.companieshouse.api.officer.DateOfBirth;

public class OfficerSearchDocument {

    public static final String RESOURCE_KIND = "searchresults#officer";

    @JsonProperty("active_count")
    private final long activeCount;
    @JsonProperty("date_of_birth")
    private final DateOfBirth dateOfBirth;
    @JsonProperty("inactive_count")
    private final long inactiveCount;
    @JsonProperty("items")
    private List<OfficerSearchAppointment> items;
    @JsonProperty("kind")
    private final String kind;
    @JsonProperty("links")
    private final OfficerSearchLinks links;
    @JsonProperty("resigned_count")
    private final long resignedCount;
    @JsonProperty("sort_key")
    private final String sortKey;

    private OfficerSearchDocument(Builder builder) {
        activeCount = builder.activeCount;
        dateOfBirth = builder.dateOfBirth;
        inactiveCount = builder.inactiveCount;
        items = builder.items;
        kind = builder.kind;
        links = builder.links;
        resignedCount = builder.resignedCount;
        sortKey = builder.sortKey;
    }

    public long getActiveCount() {
        return activeCount;
    }

    public DateOfBirth getDateOfBirth() {
        return dateOfBirth;
    }

    public long getInactiveCount() {
        return inactiveCount;
    }

    public List<OfficerSearchAppointment> getItems() {
        return items;
    }

    public OfficerSearchDocument items(List<OfficerSearchAppointment> items) {
        this.items = items;
        return this;
    }

    public OfficerSearchLinks getLinks() {
        return links;
    }

    public long getResignedCount() {
        return resignedCount;
    }

    public String getSortKey() {
        return sortKey;
    }

    public static final class Builder {

        private long activeCount;
        private DateOfBirth dateOfBirth;
        private long inactiveCount;
        private List<OfficerSearchAppointment> items;
        private final String kind;
        private OfficerSearchLinks links;
        private long resignedCount;
        private String sortKey;

        private Builder() {
            this.kind = RESOURCE_KIND;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder activeCount(long activeCount) {
            this.activeCount = activeCount;
            return this;
        }

        public Builder dateOfBirth(DateOfBirth dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder inactiveCount(long inactiveCount) {
            this.inactiveCount = inactiveCount;
            return this;
        }

        public Builder items(List<OfficerSearchAppointment> items) {
            this.items = items;
            return this;
        }

        public Builder links(OfficerSearchLinks links) {
            this.links = links;
            return this;
        }

        public Builder resignedCount(long resignedCount) {
            this.resignedCount = resignedCount;
            return this;
        }

        public Builder sortKey(String sortKey) {
            this.sortKey = sortKey;
            return this;
        }

        public OfficerSearchDocument build() {
            return new OfficerSearchDocument(this);
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
        OfficerSearchDocument document = (OfficerSearchDocument) o;
        return activeCount == document.activeCount && inactiveCount == document.inactiveCount
                && resignedCount == document.resignedCount && Objects.equals(dateOfBirth, document.dateOfBirth)
                && Objects.equals(items, document.items) && Objects.equals(kind, document.kind)
                && Objects.equals(links, document.links) && Objects.equals(sortKey, document.sortKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeCount, dateOfBirth, inactiveCount, items, kind, links, resignedCount, sortKey);
    }
}
