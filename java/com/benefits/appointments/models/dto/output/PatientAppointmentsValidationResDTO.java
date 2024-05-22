package com.benefits.appointments.models.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientAppointmentsValidationResDTO {

  int appointments;
  int cancelledAppointments;
  boolean scheduledForCurrentMonth;

}
