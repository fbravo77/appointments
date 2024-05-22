package com.benefits.appointments.models.dto.output;

import com.benefits.appointments.models.entities.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
public class AppointmentsResDto {

  private Long id;
  private String startDate;
  private String endDate;
  private String tittle;
  private boolean remote;
  private String googleMeeting;
  private String site;
  private boolean attended;
  private int patientState;
  private String comments;
  private boolean isExpired;
  private String location;
  private Boolean isCanceled;
  private String patientWid;
  private String patientFullName;
  private String specialistWid;
  private String specialistFullName;
  private Integer appointmentNumber;
  private String appointmentStatus;

  public AppointmentsResDto (Appointment appointment){
    this.id = appointment.getId();
    this.startDate = appointment.getStartDate().toString();
    this.endDate = appointment.getEndDate().toString();
    this.remote = appointment.getIsRemote() != null && appointment.getIsRemote();
    this.tittle = appointment.getTittle();
    this.patientWid = appointment.getPatient().getWorkday();
    this.patientFullName = appointment.getPatient().getPatient().getPreferredName();
    this.specialistWid = appointment.getSpecialist().getWorkday();
    this.specialistFullName = appointment.getSpecialist().getFirstName() + " " + appointment.getSpecialist().getLastName();
    this.attended = appointment.getAttended() != null && appointment.getAttended();
    this.comments = appointment.getComments();
    this.isExpired = appointment.getIsExpired() != null && appointment.getIsExpired();
    this.googleMeeting = appointment.getGoogleMeeting();
    this.location = appointment.getSite() != null ? appointment.getSite().getName() : null;
    this.isCanceled = appointment.getCancelled();
    this.appointmentNumber = appointment.getAppointmentNumber();
    this.appointmentStatus = appointment.getAppointmentStatus();
  }
}
