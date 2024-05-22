package com.benefits.appointments.models.entities;

import com.benefits.appointments.security.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Appointments")
public class Appointment extends BaseEntity{

  @JsonIgnore
  @ManyToOne
  private User patient;
  @JsonIgnore
  @ManyToOne
  private User specialist;
  @JsonIgnore
  @ManyToOne
  private Site site;
  private String googleMeeting;
  private String tittle;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Boolean isRemote;
  private Boolean attended;
  private Integer patientState;
  private String comments;
  private Boolean isExpired;
  private Boolean reminderSent;
  private Boolean confirmationSent;
  private Boolean confirmed;
  private Boolean cancelled;
  private String appointmentReason;
  private Boolean emergencyAppointment;
  private Integer appointmentNumber;
  private String appointmentStatus;
}
