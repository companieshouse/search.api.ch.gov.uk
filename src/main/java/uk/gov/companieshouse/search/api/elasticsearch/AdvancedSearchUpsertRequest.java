package uk.gov.companieshouse.search.api.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.RegisteredOfficeAddressApi;

import java.io.IOException;
import java.util.Arrays;

public class AdvancedSearchUpsertRequest {

    private static final String ID = "ID";
    private static final String COMPANY_TYPE = "company_type";
    private static final String CURRENT_COMPANY = "current_company";
    private static final String ITEMS = "items";
    private static final String COMPANY_NUMBER = "company_number";
    private static final String COMPANY_STATUS = "company_status";
    private static final String CORPORATE_NAME = "corporate_name";
    private static final String RECORD_TYPE = "record_type";
    private static final String RECORD_TYPE_VALUE = "companies";
    private static final String LINKS = "links";
    private static final String SELF = "self";
    private static final String ORDERED_ALPHA_KEY = "ordered_alpha_key";
    private static final String ORDERED_ALPHA_KEY_WITH_ID = "ordered_alpha_key_with_id";
    private static final String CORPORATE_NAME_START = "corporate_name_start";
    private static final String SIC_CODES = "sic_codes";
    private static final String ADDRESS = "address";
    private static final String ADDRESS_LINE_1 = "address_line_1";
    private static final String ADDRESS_LINE_2 = "address_line_2";
    private static final String POSTAL_CODE = "postal_code";
    private static final String LOCALITY = "locality";
    private static final String WILDCARD_KEY = "wildcard_key";
    private static final String DATE_OF_CREATION = "date_of_creation";
    private static final String DATE_OF_CESSATION = "date_of_cessation";

    public XContentBuilder buildRequest(
        CompanyProfileApi company,
        String orderedAlphaKey,
        String orderedAlphaKeyWithID) throws IOException {

        RegisteredOfficeAddressApi registeredOfficeAddress = company.getRegisteredOfficeAddress();

        return jsonBuilder()
            .startObject()
            .field(COMPANY_TYPE, company.getType())
            .startObject(CURRENT_COMPANY)
            .field(CORPORATE_NAME_START, stripCompanyEnding(company.getCompanyName()))
            .field(CORPORATE_NAME, company.getCompanyName())
            .array(SIC_CODES, company.getSicCodes())
            .field(COMPANY_NUMBER, company.getCompanyNumber())
            .field(COMPANY_STATUS, company.getCompanyStatus())
            .startObject(ADDRESS)
            .field(ADDRESS_LINE_1, registeredOfficeAddress.getAddressLine1())
            .field(ADDRESS_LINE_2, registeredOfficeAddress.getAddressLine2())
            .field(POSTAL_CODE, registeredOfficeAddress.getPostalCode())
            .field(LOCALITY, registeredOfficeAddress.getLocality())
            .endObject()
            .field(WILDCARD_KEY, orderedAlphaKey)
            .field(DATE_OF_CREATION, company.getDateOfCreation())
            .field(DATE_OF_CESSATION, company.getDateOfCessation())
            .endObject()
            .endObject();
    }

    private String stripCompanyEnding(String companyName){
        if (companyName.contains(" ") && Arrays.stream(CORPORATE_NAME_ENDINGS)
            .anyMatch((e) -> companyName.toUpperCase().endsWith(e))){
            return companyName.substring(0, companyName.lastIndexOf(' '));
        }
        return companyName;
    }

    static final String[] CORPORATE_NAME_ENDINGS = {
        "PCC LTD",
        "PCC LIMITED",
        "PROTECTED CELL COMPANY",
        "+ COMPANY UNLTD",
        "AND COMPANY LLP",
        "AND COMPANY LTD",
        "AND COMPANY PLC",
        "COMPANY LIMITED",
        "LIMITED COMPANY",
        "& COMPANY UNLTD.",
        "AND CO UNLIMITED",
        "& COMPANY LIMITED",
        "+ COMPANY UNLTD",
        "AND COMPANY UNLTD",
        "& COMPANY UNLIMITED",
        "+ COMPANY LIMITED",
        "AND COMPANY LIMITED",
        "LIMITED PARTNERSHIP",
        "LIMITED PARTNERSHIPS",
        "AND COMPANY UNLIMITED",
        "COMMUNITY INTEREST PLC",
        "PUBLIC LIMITED COMPANY",
        "COMMUNITY INTEREST P.L.C",
        "CO PUBLIC LIMITED COMPANY",
        "AND PUBLIC LIMITED COMPANY",
        "COMMUNITY INTEREST COMPANY",
        "& CO PUBLIC LIMITED COMPANY",
        "+ CO PUBLIC LIMITED COMPANY",
        "AND CO PUBLIC LIMITED COMPANY",
        "LIMITED LIABILITY PARTNERSHIP",
        "OPEN-ENDED INVESTMENT COMPANY",
        "COMPANY PUBLIC LIMITED COMPANY",
        "& COMPANY PUBLIC LIMITED COMPANY",
        "+ COMPANY PUBLIC LIMITED COMPANY",
        "CO LIMITED LIABILITY PARTNERSHIP",
        "& CO LIMITED LIABILITY PARTNERSHIP",
        "+ CO LIMITED LIABILITY PARTNERSHIP",
        "AND COMPANY PUBLIC LIMITED COMPANY",
        "EUROPEAN ECONOMIC INTEREST GROUPING",
        "AND CO LIMITED LIABILITY PARTNERSHIP",
        "COMPANY LIMITED LIABILITY PARTNERSHIP",
        "& COMPANY LIMITED LIABILITY PARTNERSHIP",
        "CO",
        "LP",
        "CBP",
        "CIC",
        "LLP",
        "LTD",
        "PLC",
        "& CO",
        "+ CO",
        "EEIG",
        "EESV",
        "EOFG",
        "EOOS",
        "GEIE",
        "GELE",
        "ICVC",
        "LTD.",
        "NULL",
        "OEIC",
        "C.I.C",
        "P.L.C",
        "UNLTD",
        "AND CO",
        "CO LLP",
        "CO LTD",
        "CO PLC",
        "P.L.C.",
        "UNLTD.",
        "COMPANY",
        "LIMITED",
        "& CO LTD",
        "& CO PLC",
        "+ CO LTD",
        "+ CO PLC",
        "& COMPANY",
        "+ COMPANY",
        "UNLIMITED",
        "& CO UNLTD",
        "+ CO UNLTD",
        "AND CO LLP",
        "AND CO LTD",
        "AND CO PLC",
        "CO LIMITED",
        "& CO UNLTD.",
        "+ CO UNLTD.",
        "AND COMPANY",
        "COMPANY LLP",
        "COMPANY LTD",
        "COMPANY PLC",
        "& AND CO LLP",
        "& CO LIMITED",
        "+ CO LIMITED",
        "AND CO UNLTD",
        "& COMPANY LLP",
        "& COMPANY PLC",
        "+ COMPANY LLP",
        "+ COMPANY LTD",
        "+ COMPANY LIMITED LIABILITY PARTNERSHIP",
        "INVESTMENT COMPANY WITH VARIABLE CAPITAL",
        "AND COMPANY LIMITED LIABILITY PARTNERSHIP",
        "COMMUNITY INTEREST PUBLIC LIMITED COMPANY",
        "L.P.",
        "LIMITED.",
        ".LTD",
        "COMPANY LTD.",
        "INVALID ENDING",
        "& CO. LIMITED",
        "PARTNERSHIP",
        "CO.LIMITED",
        "CO. LIMITED",
        "CO.LTD",
        "& CO. LTD",
        "CO. LTD",
        "CO. LTD.",
        "CO.",
        "& CO.",
        "CO.",
        "AND CO. LTD.",
        "& CO. LTD.",
        "COMPANY",
        "CO.",
        "CO LTD.",
        "CO.",
        "AND CO. LIMITED",
        "AND CO. LTD",
        "+ COMPANY PLC",
        "AND CO UNLTD.",
        "& CO UNLIMITED",
        "+ CO UNLIMITED",
        "AND CO LIMITED",
        "& COMPANY UNLTD",
        "& COMPANY LTD",
        "& CO. LIMITED.",
        "AND CO.LIMITED",
        "& CO.LIMITED",
        "AND COMPANY LTD.",
        "& CO.",
        "& CO LLP",
        "+ CO. LIMITED",
        "& COMPANY",
        "& CO.",
        "+ CO. LTD",
        "AND CO LTD.",
        "& CO.",
        "AND CO.",
        "AND CO.",
        "AND CO.LTD",
        "&  CO. LIMITED",
        "& COMPANY",
        "& CO.LTD",
        "+ CO LLP",
        "& CO",
        "AND COMPANY P.L.C.",
        "CO.",
        "COMPANY  LIMITED",
        "& COMPANY",
        "CO",
        "& CO. LIMITED",
        "CO; LIMITED",
        "UN LIMITED",
        "P L C",
        "& CO. LIMITED",
        ".CO.",
        ".CO.",
        "C.I.C",
        "COMPANY. LIMITED",
        "& COMPANY. LIMITED",
        "AND COMPANY  LIMITED",
        "COMPANY LIMITED",
        "COMPANY",
        "COMPANY LTD..",
        "& CO",
        "COMPANY P.L.C.",
        "COMPANY LIMITED.",
        "& COMPANY P.L.C.",
        "COMPANY P L C",
        "AND COMPANY LIMITED",
        "CO.LIMITED.",
        "COMPANY P.L.C.",
        "& CO.LIMITED.",
        "CO",
        "& CO. PUBLIC LIMITED COMPANY",
        "&CO. LIMITED",
        "& CO  LIMITED",
        "& CO.LTD.",
        "& COMPANY LIMITED.",
        "CO  LIMITED",
        "& COMPANY  LIMITED",
        "&  CO LIMITED",
        "CO.  LIMITED",
        "CO.LTD.",
        "E.E.I.G.",
        "CO  LTD",
        "COMPANY  LTD",
        "P.L.C",
        ",LIMITED",
        "COMPANY P.L.C",
        "COMPANY",
        "CO.",
        "AND COMPANY",
        "COMPANY(THE)LIMITED",
        "CO",
        "COMPANYLIMITED",
        "CO",
        "CO.",
        "& CO.)LIMITED",
        "& CO.)LIMITED.",
        "COMPANY.LIMITED",
        "COMPANY",
        "COMPANY)LIMITED",
        "LTD.CO.",
        "& COMPANY.LIMITED",
        "THE)LIMITED",
        "&& CO.LIMITED",
        "COMPANY UN-LIMITED",
        "CO.PLC",
        "COMPANY",
        "AND COMPANY.LIMITED",
        "& CO;LIMITED",
        "CO",
        "& CO..LIMITED",
        "COMPANY",
        "AND COMPANYLIMITED",
        "CO",
        "COMPANY",
        "LTD",
        "PLC.",
        "& CO LTD.",
        "& COMPANY LTD.",
        "SE",
        "CCG CYF",
        "CCG CYFYNGEDIG",
        "CWMNI CELL GWARCHODEDIG",
        "AR CWMNI PAC",
        "AR CWMNI CYF",
        "AR CWMNI CCC",
        "CWMNI CYFYNGEDIG",
        "CYFYNGEDIG CWMNI",
        "& CWMNI CYFYNGEDIG",
        "+CWMNI CYFYNGEDIG",
        "AR CWMNI CYFYNGEDIG",
        "PARTNERIAETH CYFYNGEDIG",
        "CWMNI BUDDIANT CYMUNEDOL CCC",
        "CWMNI CYFYNGEDIG CYHOEDDUS",
        "CWMNI BUDDIANT CYMUNEDOL C.C.C",
        "AR CWMNI CYFYNGEDIG CYHOEDDUS",
        "CWMNI BUDDIANT CYMUNEDOL",
        "PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "CWMNIBUDDSODDIANTPENAGORED",
        "CWMNI CWMNI CYFYNGEDIG CYHOEDDUS",
        "&CWMNI CWMNI CYFYNGEDIG CYHOEDDUS",
        "+CWMNI CWMNI CYFYNGEDIG CYHOEDDUS",
        "CNI PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "& CNI PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "+ CNI PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "AR CWMNI CWMNI CYFYNGEDIG CYHOEDDUS",
        "AR CNI PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "CWMNI PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "& CWMNI PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "CBC",
        "PAC",
        "CYF",
        "CCC",
        "CBCN",
        "CYF.",
        "NULL",
        "C.B.C",
        "C.C.C",
        "CNI PAC",
        "C.C.C.",
        "CWMNI",
        "CYFYNGEDIG",
        "& CWMNI",
        "+ CWMNI",
        "ANGHYFYNGEDIG",
        "AR CNI PAC",
        "AR CWMNI",
        "CWMNI PAC",
        "CWMNI CYF",
        "CWMNI CCC",
        "& AR CNI PAC",
        "& CWMNI PAC",
        "& CWMNI CCC",
        "+ CWMNI PAC",
        "+ CWMNI CYF",
        "+ CWMNI PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "CWMNIBUDDSODDIACHYFALAFNEWIDIOL",
        "AR CWMNI PARTNERIAETH ATEBOLRWYDD CYFYNGEDIG",
        "CWMNI BUDDIANT CYMUNEDOL CYHOEDDUS CYFYNGEDIG",
        "+CWMNI CCC"
    };
}


