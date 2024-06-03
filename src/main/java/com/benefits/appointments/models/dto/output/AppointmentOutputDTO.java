package com.benefits.appointments.models.dto.output;

import com.benefits.appointments.models.entities.Appointment;
import lombok.Data;
@Data
public class AppointmentOutputDTO {

  private Long id;
  private String startDate;
  private String endDate;
  private String title;
  private boolean remote;
  private String googleMeeting;
  private String site;
  private boolean attended;
  private Integer patientState;
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
  private String appointmentReason;
  private boolean emergencyAppointment;
  private boolean specialistUpdated;

  public AppointmentOutputDTO(Appointment appointment){
    this.id = appointment.getId();
    this.startDate = appointment.getStartDate().toString();
    this.endDate = appointment.getEndDate().toString();
    this.remote = appointment.isRemote();
    this.title = appointment.getTitle();
    this.patientWid = appointment.getPatient().getWorkday();
    this.patientFullName = appointment.getPatient().getPatient().getPreferredName();
    this.specialistWid = appointment.getSpecialist().getWorkday();
    this.specialistFullName = appointment.getSpecialist().getFirstName() + " " + appointment.getSpecialist().getLastName();
    this.attended = appointment.isAttended();
    this.comments = appointment.getComments();
    this.isExpired = appointment.isExpired();
    this.googleMeeting = appointment.getGoogleMeeting();
    this.location = appointment.getSite() != null ? appointment.getSite().getName() : null;
    this.isCanceled = appointment.isCancelled();
    this.appointmentNumber = appointment.getAppointmentNumber();
    this.appointmentStatus = appointment.getAppointmentStatus();
    this.appointmentReason = appointment.getAppointmentReason();
    this.patientState = appointment.getPatientState() == null ? null : appointment.getPatientState();
    this.emergencyAppointment = appointment.isEmergencyAppointment();
    this.specialistUpdated = appointment.isSpecialistUpdated();
  }
}
