package uk.gov.companieshouse.search.api.model.psc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PscSearchDocument {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("person_name")
    private String personName;
    @JsonProperty("person_title_name")
    private String personTitleName;
    @JsonProperty("full_address")
    private String fullAddress;
    @JsonProperty("forename")
    private String forename;
    @JsonProperty("other_forenames")
    private String otherForenames;
    @JsonProperty("surname")
    private String surname;
    @JsonProperty("title")
    private String title;
    @JsonProperty("date_of_birth")
    private String dateOfBirth;
    @JsonProperty("notified_on")
    private String notifiedOn;
    @JsonProperty("cessated_on")
    private String cessatedOn;
    @JsonProperty("last_cessated_on")
    private String lastCessatedOn;
    @JsonProperty("nationality")
    private String nationality;
    @JsonProperty("natures_of_control")
    private List<String> naturesOfControl;
    @JsonProperty("country_of_residence")
    private String countryOfResidence;
    @JsonProperty("wildcard_key")
    private String wildcardKey;
    @JsonProperty("sort_key")
    private String sortKey;
    @JsonProperty("record_type")
    private String recordType;
    @JsonProperty("kind")
    private String kind;
    @JsonProperty("links")
    private Object links;
    @JsonProperty("active_count")
    private Integer activeCount;
    @JsonProperty("inactive_count")
    private Integer inactiveCount;
    @JsonProperty("resigned_count")
    private Integer resignedCount;
    // Add any other fields needed

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPersonName() {
        return personName;
    }
    public void setPersonName(String personName) {
        this.personName = personName;
    }
    public String getPersonTitleName() {
        return personTitleName;
    }
    public void setPersonTitleName(String personTitleName) {
        this.personTitleName = personTitleName;
    }
    public String getFullAddress() {
        return fullAddress;
    }
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
    public String getForename() {
        return forename;
    }
    public void setForename(String forename) {
        this.forename = forename;
    }
    public String getOtherForenames() {
        return otherForenames;
    }
    public void setOtherForenames(String otherForenames) {
        this.otherForenames = otherForenames;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getNotifiedOn() {
        return notifiedOn;
    }
    public void setNotifiedOn(String notifiedOn) {
        this.notifiedOn = notifiedOn;
    }
    public String getCessatedOn() {
        return cessatedOn;
    }
    public void setCessatedOn(String cessatedOn) {
        this.cessatedOn = cessatedOn;
    }
    public String getLastCessatedOn() {
        return lastCessatedOn;
    }
    public void setLastCessatedOn(String lastCessatedOn) {
        this.lastCessatedOn = lastCessatedOn;
    }
    public String getNationality() {
        return nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public List<String> getNaturesOfControl() {
        return naturesOfControl;
    }
    public void setNaturesOfControl(List<String> naturesOfControl) {
        this.naturesOfControl = naturesOfControl;
    }
    public String getCountryOfResidence() {
        return countryOfResidence;
    }
    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }
    public String getWildcardKey() {
        return wildcardKey;
    }
    public void setWildcardKey(String wildcardKey) {
        this.wildcardKey = wildcardKey;
    }
    public String getSortKey() {
        return sortKey;
    }
    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
    public String getRecordType() {
        return recordType;
    }
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public Object getLinks() {
        return links;
    }
    public void setLinks(Object links) {
        this.links = links;
    }
    public Integer getActiveCount() {
        return activeCount;
    }
    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
    }
    public Integer getInactiveCount() {
        return inactiveCount;
    }
    public void setInactiveCount(Integer inactiveCount) {
        this.inactiveCount = inactiveCount;
    }
    public Integer getResignedCount() {
        return resignedCount;
    }
    public void setResignedCount(Integer resignedCount) {
        this.resignedCount = resignedCount;
    }
}
