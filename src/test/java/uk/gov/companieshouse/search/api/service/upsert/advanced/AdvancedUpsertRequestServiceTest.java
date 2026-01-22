package uk.gov.companieshouse.search.api.service.upsert.advanced;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.RegisteredOfficeAddressApi;
import uk.gov.companieshouse.search.api.elasticsearch.AdvancedSearchUpsertRequest;
import uk.gov.companieshouse.search.api.exception.UpsertException;
import uk.gov.companieshouse.search.api.util.ConfiguredIndexNamesProvider;

@ExtendWith(MockitoExtension.class)
class AdvancedUpsertRequestServiceTest {

    @Mock
    private AdvancedSearchUpsertRequest mockAdvancedSearchUpsertRequest;

    @Mock
    private ConfiguredIndexNamesProvider indices;

    @InjectMocks
    private AdvancedUpsertRequestService advancedUpsertRequestService;

    private static final String ADVANCED_SEARCH = "advanced_search";
    private static final String ORDERED_ALPHA_KEY_FIELD = "orderedAlphaKey";
    private static final String SAME_AS_ALPHA_KEY_FIELD = "sameAsAlphaKey";
    private static final String COMPANY_TYPE = "ltd";
    private static final String COMPANY_SUBTYPE = "test subtype";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String COMPANY_STATUS = "active";
    private static final String COMPANY_NAME = "test company ltd";
    private static final String[] SIC_CODES = {"12345"};
    private static final String ADDRESS_LINE_1 = "address line 1";
    private static final String ADDRESS_LINE_2 = "address line 2";
    private static final String POSTAL_CODE = "postal_code";
    private static final String LOCALITY = "locality";
    private static final LocalDate DATE_OF_CREATION = LocalDate.of(1983, 01, 01);
    private static final LocalDate DATE_OF_CESSATION = LocalDate.of(1993, 01, 01);
    private static final String FULL_ADDRESS = ADDRESS_LINE_1 + ", " + ADDRESS_LINE_2 + ", " + POSTAL_CODE;
    private static final String SAME_AS = "sameAs";

    private static final String COMPANY_TYPE_KEY = "company_type";
    private static final String CURRENT_COMPANY_KEY = "current_company";
    private static final String ITEMS_KEY = "items";
    private static final String COMPANY_NUMBER_KEY = "company_number";
    private static final String COMPANY_STATUS_KEY = "company_status";
    private static final String CORPORATE_NAME_KEY = "corporate_name";
    private static final String RECORD_TYPE_KEY = "record_type";
    private static final String RECORD_TYPE_VALUE = "companies";
    private static final String LINKS_KEY = "links";
    private static final String SELF_KEY = "self";
    private static final String SIC_CODES_KEY = "sic_codes";
    private static final String ADDRESS_KEY = "address";
    private static final String PREMISES_KEY = "premises";
    private static final String ADDRESS_LINE_1_KEY = "address_line_1";
    private static final String ADDRESS_LINE_2_KEY = "address_line_2";
    private static final String POSTAL_CODE_KEY = "postal_code";
    private static final String LOCALITY_KEY = "locality";
    private static final String REGION_KEY = "region";
    private static final String COUNTRY_KEY = "country";
    private static final String WILDCARD_KEY = "wildcard_key";
    private static final String DATE_OF_CREATION_KEY = "date_of_creation";
    private static final String DATE_OF_CESSATION_KEY = "date_of_cessation";
    private static final String FULL_ADDRESS_KEY = "full_address";
    private static final String SAME_AS_KEY = "same_as_key";
    private static final String KIND_KEY = "kind";
    private static final String KIND_VALUE = "searchresults#company";
    private static final String SORT_KEY = "sort_key";

    @BeforeEach
    void init() {
        when(indices.advanced()).thenReturn(ADVANCED_SEARCH);
    }

    @Test
    @DisplayName("Test create index and update request is successful")
    void testCreateIndexRequestSuccessful() throws Exception {

        CompanyProfileApi company = createCompany();

        when(mockAdvancedSearchUpsertRequest.buildRequest(company, ORDERED_ALPHA_KEY_FIELD,
            SAME_AS_ALPHA_KEY_FIELD))
            .thenReturn(createRequest(company, ORDERED_ALPHA_KEY_FIELD
            ));

        UpdateRequest updateRequest = advancedUpsertRequestService
            .createUpdateRequest(company, ORDERED_ALPHA_KEY_FIELD, SAME_AS_ALPHA_KEY_FIELD);

        assertNotNull(updateRequest);
        assertEquals(ADVANCED_SEARCH, updateRequest.index());
    }

    @Test
    @DisplayName("Test create update request throws exception")
    void testUpdateIndexThrowsException() throws Exception {

        CompanyProfileApi company = createCompany();

        when(mockAdvancedSearchUpsertRequest.buildRequest(company, ORDERED_ALPHA_KEY_FIELD,
            SAME_AS_ALPHA_KEY_FIELD)).thenThrow(IOException.class);

        assertThrows(UpsertException.class,
            () -> advancedUpsertRequestService
                .createUpdateRequest(company, ORDERED_ALPHA_KEY_FIELD, SAME_AS_ALPHA_KEY_FIELD));
    }

    private CompanyProfileApi createCompany() {
        CompanyProfileApi company = new CompanyProfileApi();
        company.setType(COMPANY_TYPE);
        company.setCompanyName(COMPANY_NAME);
        company.setSicCodes(SIC_CODES);
        company.setCompanyNumber(COMPANY_NUMBER);
        company.setCompanyStatus(COMPANY_STATUS);

        RegisteredOfficeAddressApi registeredOfficeAddressApi = new RegisteredOfficeAddressApi();
        registeredOfficeAddressApi.setAddressLine1(ADDRESS_LINE_1);
        registeredOfficeAddressApi.setAddressLine2(ADDRESS_LINE_2);
        registeredOfficeAddressApi.setPostalCode(POSTAL_CODE);
        registeredOfficeAddressApi.setLocality(LOCALITY);
        company.setRegisteredOfficeAddress(registeredOfficeAddressApi);

        company.setDateOfCreation(DATE_OF_CREATION);
        company.setDateOfCessation(DATE_OF_CESSATION);

        Map<String, String> links = new HashMap<>();
        links.put("self", "company/00000000");
        company.setLinks(links);

        return company;
    }

    private XContentBuilder createRequest(CompanyProfileApi company, String orderedAlphaKey) throws Exception {
        RegisteredOfficeAddressApi registeredOfficeAddress = company.getRegisteredOfficeAddress();
        Map<String, String> links = company.getLinks();

        return jsonBuilder()
            .startObject()
                .field(COMPANY_TYPE_KEY, company.getType())
                .field(COMPANY_SUBTYPE, company.getSubtype())
                .startObject(CURRENT_COMPANY_KEY)
                    .field(CORPORATE_NAME_KEY, company.getCompanyName())
                    .array(SIC_CODES_KEY, company.getSicCodes())
                    .field(COMPANY_NUMBER_KEY, company.getCompanyNumber())
                    .field(COMPANY_STATUS_KEY, company.getCompanyStatus())
                    .startObject(ADDRESS_KEY)
                        .field(PREMISES_KEY, registeredOfficeAddress.getPremises())
                        .field(ADDRESS_LINE_1_KEY, registeredOfficeAddress.getAddressLine1())
                        .field(ADDRESS_LINE_2_KEY, registeredOfficeAddress.getAddressLine2())
                        .field(POSTAL_CODE_KEY, registeredOfficeAddress.getPostalCode())
                        .field(LOCALITY_KEY, registeredOfficeAddress.getLocality())
                        .field(REGION_KEY, registeredOfficeAddress.getRegion())
                        .field(COUNTRY_KEY, registeredOfficeAddress.getCountry())
                    .endObject()
                    .field(WILDCARD_KEY, orderedAlphaKey)
                    .field(DATE_OF_CREATION_KEY, company.getDateOfCreation())
                    .field(DATE_OF_CESSATION_KEY, company.getDateOfCessation())
                    .field(FULL_ADDRESS_KEY, FULL_ADDRESS)
                    .field(RECORD_TYPE_KEY, RECORD_TYPE_VALUE)
                    .field(SAME_AS_KEY, SAME_AS)
                .endObject()
                .startObject(ITEMS_KEY)
                    .startObject(ADDRESS_KEY)
                        .field(PREMISES_KEY, registeredOfficeAddress.getPremises())
                        .field(ADDRESS_LINE_1_KEY, registeredOfficeAddress.getAddressLine1())
                        .field(ADDRESS_LINE_2_KEY, registeredOfficeAddress.getAddressLine2())
                        .field(POSTAL_CODE_KEY, registeredOfficeAddress.getPostalCode())
                        .field(LOCALITY_KEY, registeredOfficeAddress.getLocality())
                        .field(REGION_KEY, registeredOfficeAddress.getRegion())
                        .field(COUNTRY_KEY, registeredOfficeAddress.getCountry())
                    .endObject()
                    .field(COMPANY_NUMBER_KEY, company.getCompanyNumber())
                    .field(COMPANY_STATUS_KEY, company.getCompanyStatus())
                    .field(CORPORATE_NAME_KEY, company.getCompanyName())
                    .array(SIC_CODES_KEY, company.getSicCodes())
                    .field(DATE_OF_CREATION_KEY, company.getDateOfCreation())
                    .field(DATE_OF_CESSATION_KEY, company.getDateOfCessation())
                    .field(FULL_ADDRESS_KEY, FULL_ADDRESS)
                    .field(SAME_AS_KEY, SAME_AS)
                    .field(WILDCARD_KEY, orderedAlphaKey)
                    .field(RECORD_TYPE_KEY, RECORD_TYPE_VALUE)
                .endObject()
                .field(KIND_KEY, KIND_VALUE)
                .startObject(LINKS_KEY)
                    .field(SELF_KEY, links.get(SELF_KEY))
                .endObject()
                .field(SORT_KEY, orderedAlphaKey + "0")
            .endObject();
    }
}
