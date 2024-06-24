package uk.gov.companieshouse.search.api.model.esdatamodel;

import java.util.Objects;
import uk.gov.companieshouse.api.company.Data;

public class CompanyItemFullDataConverterModel {

    private CompanySearchItemData companySearchItemData;

    private Data companyData;

    private String alphaKey;

    public CompanySearchItemData getCompanySearchRequiredData() {
        return companySearchItemData;
    }

    public Data getCompanyData() {
        return companyData;
    }

    public String getAlphaKey() {
        return alphaKey;
    }

    public CompanyItemFullDataConverterModel companySearchData(
            CompanySearchItemData companySearchItemData) {
        this.companySearchItemData = companySearchItemData;
        return this;
    }

    public CompanyItemFullDataConverterModel companyData(
            Data companyData) {
        this.companyData = companyData;
        return this;
    }

    public CompanyItemFullDataConverterModel alphaKey(
            String alphaKey) {
        this.alphaKey = alphaKey;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanyItemFullDataConverterModel that = (CompanyItemFullDataConverterModel) o;
        return Objects.equals(companySearchItemData, that.companySearchItemData) && Objects.equals(
                companyData, that.companyData) && Objects.equals(alphaKey, that.alphaKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companySearchItemData, companyData, alphaKey);
    }
}
