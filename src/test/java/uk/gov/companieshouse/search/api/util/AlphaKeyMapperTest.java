package uk.gov.companieshouse.search.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.api.officer.NameElements;
import uk.gov.companieshouse.api.officer.OfficerAppointmentSummary;
import uk.gov.companieshouse.search.api.model.response.AlphaKeyResponse;
import uk.gov.companieshouse.search.api.service.AlphaKeyService;

@ExtendWith(MockitoExtension.class)
class AlphaKeyMapperTest {

    @InjectMocks
    private AlphaKeyMapper alphaKeyMapper;
    @Mock
    private AlphaKeyService alphaKeyService;

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
        verifyNoInteractions(alphaKeyService);
    }

    @Test
    void makeSortKeyCorporate() {
        // given
        AppointmentList appointmentList = new AppointmentList()
                .isCorporateOfficer(true)
                .items(Collections.singletonList(new OfficerAppointmentSummary()
                        .name("corporate officer name ltd")
                ));
        AlphaKeyResponse response = new AlphaKeyResponse();
        response.setOrderedAlphaKey("corporate officer name ltd");

        when(alphaKeyService.getAlphaKeyForCorporateName(anyString())).thenReturn(response);

        // when
        String actual = alphaKeyMapper.makeSortKey(appointmentList);

        // then
        assertEquals("corporate officer name ltd2", actual);
        verify(alphaKeyService).getAlphaKeyForCorporateName("corporate officer name ltd");
    }
}