package uk.gov.companieshouse.search.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.api.officer.NameElements;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;

@ExtendWith(MockitoExtension.class)
class AlphaKeyMapperTest {

    @InjectMocks
    private AlphaKeyMapper alphaKeyMapper;

    @Test
    void makeSortKeyNatural() {
        // given
        AppointmentList appointmentList = new AppointmentList()
                .isCorporateOfficer(false)
                .items(Collections.singletonList(new OfficerAppointmentSummary()
                        .nameElements(new NameElements()
                                .surname("Smith")
                                .forename("John")
                                .otherForenames("Tester"))));

        // when
        String actual = alphaKeyMapper.makeSortKey(appointmentList);

        // then
        assertEquals("Smith John Tester2", actual);
    }

    @Test
    void makeSortKeyCorporate() {
        // given
        AppointmentList appointmentList = new AppointmentList()
                .isCorporateOfficer(true)
                .items(Collections.singletonList(new OfficerAppointmentSummary()
                        .name("corporate officer name ltd")
                ));

        // when
        String actual = alphaKeyMapper.makeSortKey(appointmentList);

        // then
        assertEquals("2", actual); // sortkey for corporate officers is just set to 2 for now
    }
}