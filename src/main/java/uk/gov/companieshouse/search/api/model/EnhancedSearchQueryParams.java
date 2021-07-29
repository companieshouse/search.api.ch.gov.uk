package uk.gov.companieshouse.search.api.model;

import java.time.LocalDate;
import java.util.List;

public class EnhancedSearchQueryParams {

    private String companyName;

    private String location;

    private LocalDate incorporatedFrom;

    private LocalDate incorporatedTo;

    private List<String> companyStatusList;

    private List<String> sicCodes;

    private List<String> companyTypeList;

    private LocalDate dissolvedFrom;

    private LocalDate dissolvedTo;

    private String companyNameExcludes;

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

    public List<String> getCompanyStatusList() {
        return companyStatusList;
    }

    public void setCompanyStatusList(List<String> companyStatusList) {
        this.companyStatusList = companyStatusList;
    }

    public List<String> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<String> sicCodes) {
        this.sicCodes = sicCodes;
    }

    public List<String> getCompanyTypeList() {
        return companyTypeList;
    }

    public void setCompanyTypeList(List<String> companyTypeList) {
        this.companyTypeList = companyTypeList;
    }

    public LocalDate getDissolvedFrom() {
        return dissolvedFrom;
    }

    public void setDissolvedFrom(LocalDate dissolvedFrom) {
        this.dissolvedFrom = dissolvedFrom;
    }

    public LocalDate getDissolvedTo() {
        return dissolvedTo;
    }

    public void setDissolvedTo(LocalDate dissolvedTo) {
        this.dissolvedTo = dissolvedTo;
    }

    public String getCompanyNameExcludes() {
        return companyNameExcludes;
    }

    public void setCompanyNameExcludes(String companyNameExcludes) {
        this.companyNameExcludes = companyNameExcludes;
    }
}
