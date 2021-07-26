package uk.gov.companieshouse.search.api.mapper;

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

    private static final List<String> companyStatuses = Arrays.asList(
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

    public EnhancedSearchQueryParams mapEnhancedQueryParameters(String companyName,
                                                                String location,
                                                                String incorporatedFrom,
                                                                String incorporatedTo,
                                                                List<String> companyStatusList,
                                                                String sicCodes) throws DateFormatException, MappingException {

        EnhancedSearchQueryParams enhancedSearchQueryParams = new EnhancedSearchQueryParams();
        enhancedSearchQueryParams.setCompanyName(companyName);
        enhancedSearchQueryParams.setLocation(location);

        try {
            if (incorporatedFrom != null) {
                enhancedSearchQueryParams.setIncorporatedFrom(LocalDate.parse(incorporatedFrom));
            }
            if (incorporatedTo != null) {
                enhancedSearchQueryParams.setIncorporatedTo(LocalDate.parse(incorporatedTo));
            }
        } catch (DateTimeParseException e) {
            throw new DateFormatException("error occurred setting date field");
        }

        if (companyStatusList != null) {
            List<String> mappedCompanyStatusList = new ArrayList<>();

            for (String status: companyStatusList) {
                if (companyStatuses.contains(status.toLowerCase())) {
                    mappedCompanyStatusList.add(status);
                } else {
                    throw new MappingException("failed to map value for company status: " + status);
                }
            }
            enhancedSearchQueryParams.setCompanyStatusList(mappedCompanyStatusList);
        }

        enhancedSearchQueryParams.setSicCodes(sicCodes);

        return enhancedSearchQueryParams;
    }
}
