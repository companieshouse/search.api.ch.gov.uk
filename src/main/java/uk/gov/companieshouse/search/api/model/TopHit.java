package uk.gov.companieshouse.search.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.search.api.model.esdatamodel.Address;
import uk.gov.companieshouse.search.api.model.esdatamodel.Links;
import uk.gov.companieshouse.search.api.model.esdatamodel.PreviousCompanyName;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopHit {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("company_status")
    private String companyStatus;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("ordered_alpha_key_with_id")
    private String orderedAlphaKeyWithId;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("record_type")
    private String recordType;

    @JsonProperty("links")
    private Links links;

    @JsonProperty("date_of_cessation")
    private LocalDate dateOfCessation;

    @JsonProperty("date_of_creation")
    private LocalDate dateOfCreation;

    @JsonProperty("registered_office_address")
    private Address registeredOfficeAddress;

    @JsonProperty("previous_company_names")
    private List<PreviousCompanyName> previousCompanyNames;

    @JsonProperty("matched_previous_company_name")
    private PreviousCompanyName matchedPreviousCompanyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getOrderedAlphaKeyWithId() {
        return orderedAlphaKeyWithId;
    }

    public void setOrderedAlphaKeyWithId(String orderedAlphaKeyWithId) {
        this.orderedAlphaKeyWithId = orderedAlphaKeyWithId;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public LocalDate getDateOfCessation() {
        return dateOfCessation;
    }

    public void setDateOfCessation(LocalDate dateOfCessation) {
        this.dateOfCessation = dateOfCessation;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Address getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public void setRegisteredOfficeAddress(Address registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public List<PreviousCompanyName> getPreviousCompanyNames() {
        return previousCompanyNames;
    }

    public void setPreviousCompanyNames(List<PreviousCompanyName> previousCompanyNames) {
        this.previousCompanyNames = previousCompanyNames;
    }

    public PreviousCompanyName getMatchedPreviousCompanyName() {
        return matchedPreviousCompanyName;
    }

    public void setMatchedPreviousCompanyName(PreviousCompanyName matchedPreviousCompanyName) {
        this.matchedPreviousCompanyName = matchedPreviousCompanyName;
    }
}
