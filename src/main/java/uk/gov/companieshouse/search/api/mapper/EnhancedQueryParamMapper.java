package uk.gov.companieshouse.search.api.mapper;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.exception.DateFormatException;
import uk.gov.companieshouse.search.api.model.EnhancedSearchQueryParams;

@Component
public class EnhancedQueryParamMapper {

    public EnhancedSearchQueryParams mapEnhancedQueryParameters(String companyName,
                                                                 String location,
                                                                 String incorporatedFrom,
                                                                 String incorporatedTo,
                                                                 List<String> sicCodes) throws DateFormatException {

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
        } catch (Exception e) {
            throw new DateFormatException("error occured setting date field");
        }

        enhancedSearchQueryParams.setSicCodes(sicCodes);

        return enhancedSearchQueryParams;
    }
}
