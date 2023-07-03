package uk.gov.companieshouse.search.api.model.esdatamodel;

import java.util.List;
import uk.gov.companieshouse.api.officer.DateOfBirth;

public class OfficerSearchDocument {

    public static final String KIND = "searchresults#officer";

    private final long activeCount;
    private final DateOfBirth dateOfBirth;
    private final long inactiveCount;
    private final List<OfficerSearchAppointment> items;
    private final String kind;
    private final OfficerSearchLinks links;
    private final long resignedCount;
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

    public String getKind() {
        return kind;
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
        private String kind;
        private OfficerSearchLinks links;
        private long resignedCount;
        private String sortKey;

        private Builder() {
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

        public Builder kind(String kind) {
            this.kind = kind;
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
}