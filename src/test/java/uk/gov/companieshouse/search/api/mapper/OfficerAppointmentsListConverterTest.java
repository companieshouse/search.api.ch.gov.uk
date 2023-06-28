package uk.gov.companieshouse.search.api.mapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.officer.AppointmentList;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchAppointment;
import uk.gov.companieshouse.search.api.model.esdatamodel.OfficerSearchLinks;

@ExtendWith(MockitoExtension.class)
class OfficerAppointmentsListConverterTest {

    @InjectMocks
    private OfficerAppointmentsListConverter fullDocumentConverter;
    @Mock
    private OfficerAppointmentSummaryConverter itemsConverter;
    @Mock
    private OfficerSearchLinks officerSearchLinks;
    @Mock
    private OfficerSearchAppointment officerSearchAppointment;

    @Test
    @DisplayName("Should successfully convert officers appointment list to officers search document")
    void postitiveTestForConversion () {
        //Given
//        when(itemsConverter.convert(any())).thenReturn(singletonList(officerSearchAppointment));


        //When - should map to officer search doc.


        //Then - take a look at company-appointments-api

    }


    private AppointmentList makeAppointmentListObject() {
        AppointmentList appointmentList = new AppointmentList();

        appointmentList.setTotalResults(3);
        appointmentList.setActiveCount(1);
        appointmentList.setInactiveCount(2);
//        appointmentList.setItems();

        return appointmentList;
    }

}
