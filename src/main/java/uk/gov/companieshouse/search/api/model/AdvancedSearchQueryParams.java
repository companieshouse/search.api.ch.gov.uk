package uk.gov.companieshouse.search.api.model;

import java.time.LocalDate;
import java.util.List;

public class AdvancedSearchQueryParams {

    private Integer startIndex;

    private String companyNameIncludes;

    private String location;

    private LocalDate incorporatedFrom;

    private LocalDate incorporatedTo;

    private List<String> companyStatusList;

    private List<String> sicCodes;

    private List<String> companyTypeList;

    private List<String> companySubtypeList;

    private LocalDate dissolvedFrom;

    private LocalDate dissolvedTo;

    private String companyNameExcludes;

    private Integer size;

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public String getCompanyNameIncludes() {
        return companyNameIncludes;
    }

    public void setCompanyNameIncludes(String companyNameIncludes) {
        this.companyNameIncludes = companyNameIncludes;
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

    public List<String> getCompanySubtypeList() {
        return companySubtypeList;
    }

    public void setCompanySubtypeList(List<String> companySubtypeList) {
        this.companySubtypeList = companySubtypeList;
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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
