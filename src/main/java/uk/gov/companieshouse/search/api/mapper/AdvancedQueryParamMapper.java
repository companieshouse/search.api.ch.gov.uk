package uk.gov.companieshouse.search.api.mapper;

import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_STATUS;
import static uk.gov.companieshouse.search.api.logging.LoggingUtils.COMPANY_TYPE;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.exception.MappingException;
import uk.gov.companieshouse.search.api.exception.SizeException;
import uk.gov.companieshouse.search.api.model.AdvancedSearchQueryParams;
import uk.gov.companieshouse.search.api.service.search.SearchRequestUtils;

@Component
public class AdvancedQueryParamMapper {

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

    @Autowired
    private EnvironmentReader environmentReader;

    private static final String ADVANCED_SEARCH_DEFAULT_SIZE = "ADVANCED_SEARCH_DEFAULT_SIZE";
    private static final String ADVANCED_SEARCH_MAX_SIZE = "ADVANCED_SEARCH_MAX_SIZE";

    public AdvancedSearchQueryParams mapAdvancedQueryParameters(Integer startIndex,
                                                                String companyNameIncludes,
                                                                String location,
                                                                String incorporatedFrom,
                                                                String incorporatedTo,
                                                                List<String> companyStatusList,
                                                                List<String> sicCodes,
                                                                List<String> companyTypeList,
                                                                String dissolvedFrom,
                                                                String dissolvedTo,
                                                                String companyNameExcludes,
                                                                Integer size)
            throws DateFormatException, MappingException, SizeException {

        AdvancedSearchQueryParams advancedSearchQueryParams = new AdvancedSearchQueryParams();

        if (startIndex == null || startIndex < 0) {
            startIndex = 0;
        }

        advancedSearchQueryParams.setStartIndex(startIndex);
        advancedSearchQueryParams.setCompanyNameIncludes(companyNameIncludes);
        advancedSearchQueryParams.setLocation(location);
        advancedSearchQueryParams.setSicCodes(sicCodes);
        mapDates(advancedSearchQueryParams, incorporatedFrom, incorporatedTo, dissolvedFrom, dissolvedTo);
        advancedSearchQueryParams.setCompanyNameExcludes(companyNameExcludes);

        if (companyStatusList != null) {
            advancedSearchQueryParams.setCompanyStatusList(
                mapListParam(companyStatusList, ACCEPTED_COMPANY_STATUS, COMPANY_STATUS));
        }

        if (companyTypeList != null) {
            advancedSearchQueryParams.setCompanyTypeList(
                mapListParam(companyTypeList, ACCEPTED_COMPANY_TYPES, COMPANY_TYPE));
        }

        try {
            advancedSearchQueryParams.setSize(SearchRequestUtils.checkResultsSize
                    (size, environmentReader.getMandatoryInteger(ADVANCED_SEARCH_DEFAULT_SIZE),
                            environmentReader.getMandatoryInteger(ADVANCED_SEARCH_MAX_SIZE)));
        } catch (SizeException se) {
            throw new SizeException("error occurred size field");
        }

        return advancedSearchQueryParams;

    }

    private void mapDates(AdvancedSearchQueryParams advancedSearchQueryParams,
                          String incorporatedFrom,
                          String incorporatedTo,
                          String dissolvedFrom,
                          String dissolvedTo) throws DateFormatException {

        try {
            if (incorporatedFrom != null) {
                advancedSearchQueryParams.setIncorporatedFrom(LocalDate.parse(incorporatedFrom));
            }
            if (incorporatedTo != null) {
                advancedSearchQueryParams.setIncorporatedTo(LocalDate.parse(incorporatedTo));
            }
            if (dissolvedFrom != null) {
                advancedSearchQueryParams.setDissolvedFrom(LocalDate.parse(dissolvedFrom));
            }
            if (dissolvedTo != null) {
                advancedSearchQueryParams.setDissolvedTo(LocalDate.parse(dissolvedTo));
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
