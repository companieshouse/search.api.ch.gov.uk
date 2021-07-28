package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_STATUS;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_TYPE;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class EnhancedQueryParamMapper {

    private static final List<String> ACCEPTED_COMPANY_STATUS = Arrays.asList(
        "active",
        "dissolved",
        "open",
        "closed",
        "converted-closed",
        "receivership",
        "administration",
        "liquidation",
        "insolvency-proceedings",
        "voluntary-arrangement"
    );

    private static final List<String> ACCEPTED_COMPANY_TYPES = Arrays.asList(
        "private-unlimited",
        "ltd",
        "plc",
        "old-public-company",
        "private-limited-guarant-nsc-limited-exemption",
        "limited-partnership",
        "private-limited-guarant-nsc",
        "converted-or-closed",
        "private-unlimited-nsc",
        "private-limited-shares-section-30-exemption",
        "protected-cell-company",
        "assurance-company",
        "oversea-company",
        "eeig-establishment",
        "icvc-securities",
        "icvc-warrant",
        "icvc-umbrella",
        "registered-society-non-jurisdictional",
        "industrial-and-provident-society",
        "northern-ireland",
        "northern-ireland-other",
        "llp",
        "royal-charter",
        "investment-company-with-variable-capital",
        "unregistered-company",
        "other",
        "european-public-limited-liability-company-se",
        "united-kingdom-societas",
        "uk-establishment",
        "scottish-partnership",
        "charitable-incorporated-organisation",
        "scottish-charitable-incorporated-organisation",
        "further-education-or-sixth-form-college-corporation",
        "eeig",
        "ukeig"
    );

    public EnhancedSearchQueryParams mapEnhancedQueryParameters(String companyName,
                                                                String location,
                                                                String incorporatedFrom,
                                                                String incorporatedTo,
                                                                List<String> companyStatusList,
                                                                List<String> sicCodes,
                                                                List<String> companyTypeList,
                                                                String dissolvedFrom,
                                                                String dissolvedTo) throws DateFormatException, MappingException {

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(companyName);
        enhancedSearchQueryParams.setLocation(location);
        enhancedSearchQueryParams.setSicCodes(sicCodes);
        mapDates(enhancedSearchQueryParams, incorporatedFrom, incorporatedTo, dissolvedFrom, dissolvedTo);

        if (companyStatusList != null) {
            enhancedSearchQueryParams.setCompanyStatusList(
                mapListParam(companyStatusList, ACCEPTED_COMPANY_STATUS, COMPANY_STATUS));
        }

        if (companyTypeList != null) {
            enhancedSearchQueryParams.setCompanyTypeList(
                mapListParam(companyTypeList, ACCEPTED_COMPANY_TYPES, COMPANY_TYPE));
        }

        return enhancedSearchQueryParams;
    }

    private void mapDates(EnhancedSearchQueryParams enhancedSearchQueryParams,
                          String incorporatedFrom,
                          String incorporatedTo,
                          String dissolvedFrom,
                          String dissolvedTo) throws DateFormatException {

        try {
            if (incorporatedFrom != null) {
                enhancedSearchQueryParams.setIncorporatedFrom(LocalDate.parse(incorporatedFrom));
            }
            if (incorporatedTo != null) {
                enhancedSearchQueryParams.setIncorporatedTo(LocalDate.parse(incorporatedTo));
            }
            if (dissolvedFrom != null) {
                enhancedSearchQueryParams.setDissolvedFrom(LocalDate.parse(dissolvedFrom));
            }
            if (dissolvedTo != null) {
                enhancedSearchQueryParams.setDissolvedTo(LocalDate.parse(dissolvedTo));
            }
        } catch (DateTimeParseException e) {
            throw new DateFormatException("error occurred setting date field");
        }
    }

    private List<String> mapListParam(List<String> paramList,
                                      List<String> acceptedStringsList,
                                      String field) throws MappingException {

        List<String> listToReturn = new ArrayList<>();
        for (String param : paramList) {
            if (acceptedStringsList.contains(param.toLowerCase())) {
                listToReturn.add(param.toLowerCase());
            } else {
                throw new MappingException("failed to map value for " + field + ": " + param);
            }
        }
        return listToReturn;
    }
}
