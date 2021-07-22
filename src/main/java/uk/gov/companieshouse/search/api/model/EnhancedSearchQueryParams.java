package uk.gov.companieshouse.search.api.model;

import java.time.LocalDate;

public class EnhancedSearchQueryParams {

    private String companyName;

    private String location;

    private LocalDate incorporatedFrom;

    private LocalDate incorporatedTo;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getIncorporatedFrom() {
        return incorporatedFrom;
    }

    public void setIncorporatedFrom(LocalDate incorporatedFrom) {
        this.incorporatedFrom = incorporatedFrom;
    }

    public LocalDate getIncorporatedTo() {
        return incorporatedTo;
    }

    public void setIncorporatedTo(LocalDate incorporatedTo) {
        this.incorporatedTo = incorporatedTo;
    }
}
