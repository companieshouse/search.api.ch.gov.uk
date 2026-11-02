package uk.gov.companieshouse.search.api.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.search.api.model.psc.PscSummary;
import uk.gov.companieshouse.search.api.model.psc.PscSearchDocument;

import java.util.StringJoiner;

@Component
public class PscSummaryConverter implements Converter<PscSummary, PscSearchDocument> {
    @Override
    public PscSearchDocument convert(PscSummary source) {
        PscSearchDocument doc = new PscSearchDocument();
        // Map direct fields
        doc.setForename(source.nameElements != null ? source.nameElements.forename : null);
        doc.setOtherForenames(source.nameElements != null ? source.nameElements.middleName : null);
        doc.setSurname(source.nameElements != null ? source.nameElements.surname : null);
        doc.setTitle(source.nameElements != null ? source.nameElements.title : null);
        doc.setPersonName(source.name);
        doc.setPersonTitleName(
            (source.nameElements != null && source.nameElements.title != null && !source.nameElements.title.isEmpty())
                ? source.nameElements.title + " " + source.name
                : source.name
        );
        doc.setDateOfBirth(source.dateOfBirth != null ?
            (source.dateOfBirth.year + "-" + String.format("%02d", source.dateOfBirth.month)) : null);
        doc.setNotifiedOn(source.notifiedOn);
        doc.setCessatedOn(source.cessatedOn);
        doc.setNationality(source.nationality);
        doc.setNaturesOfControl(source.naturesOfControl);
        doc.setCountryOfResidence(source.countryOfResidence);
        doc.setLinks(source.links);
        // Address concatenation
        if (source.address != null) {
            StringJoiner joiner = new StringJoiner(", ");
            if (source.address.premises != null) joiner.add(source.address.premises);
            if (source.address.addressLine1 != null) joiner.add(source.address.addressLine1);
            if (source.address.addressLine2 != null) joiner.add(source.address.addressLine2);
            if (source.address.locality != null) joiner.add(source.address.locality);
            if (source.address.region != null) joiner.add(source.address.region);
            if (source.address.country != null) joiner.add(source.address.country);
            if (source.address.postalCode != null) joiner.add(source.address.postalCode);
            doc.setFullAddress(joiner.toString());
        }
        // Computed fields
        String surname = doc.getSurname() != null ? doc.getSurname() : "";
        String forename = doc.getForename() != null ? doc.getForename() : "";
        String otherForenames = doc.getOtherForenames() != null ? doc.getOtherForenames() : "";
        String wildcardKey = surname + forename + otherForenames + "2";
        doc.setWildcardKey(wildcardKey);
        doc.setSortKey(wildcardKey);
        doc.setRecordType("personswithsignificantcontrol");
        doc.setKind("searchresults#persons-with-significant-control");
        // TODO: Set active_count, inactive_count, resigned_count, last_cessated_on as per officers logic
        return doc;
    }
}
