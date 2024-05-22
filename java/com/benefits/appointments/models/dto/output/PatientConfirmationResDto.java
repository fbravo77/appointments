package com.benefits.appointments.models.dto.output;

import com.benefits.appointments.models.entities.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientConfirmationResDto {

  private String patientName;
  private String specialistName;
  private String specialistOccupation;
  private String appointmentDate;
  private String location;

public PatientConfirmationResDto (Appointment appointment){
  this.appointmentDate = appointment.getStartDate().toString();
  this.patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
  this.specialistName = appointment.getSpecialist().getFirstName() + " " + appointment.getSpecialist().getLastName();
  this.specialistOccupation = appointment.getSpecialist().getSpecialist().getProfession().getOccupation().getAlias();
  this.location = appointment.getSite() != null ? appointment.getSite().getName() : "Remote";
}
}
