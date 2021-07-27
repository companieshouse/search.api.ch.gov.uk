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

    private static final List<String> acceptedCompanyStatus = Arrays.asList(
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

    private static final List<String> acceptedCompanyTypes = Arrays.asList(
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
        "eeig",
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
        "uk-establishment",
        "scottish-partnership",
        "charitable-incorporated-organisation",
        "scottish-charitable-incorporated-organisation",
        "further-education-or-sixth-form-college-corporation",
        "community-interest-company",
        "private-fund-limited-partnership"
    );

    public EnhancedSearchQueryParams mapEnhancedQueryParameters(String companyName,
                                                                String location,
                                                                String incorporatedFrom,
                                                                String incorporatedTo,
                                                                List<String> companyStatusList,
                                                                List<String> sicCodes) throws DateFormatException, MappingException {

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
                if (acceptedCompanyStatus.contains(status.toLowerCase())) {
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
