package com.benefits.appointments.models.entities;

import com.benefits.appointments.security.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
  @ManyToOne(fetch = FetchType.LAZY)
  private User patient;
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private User specialist;
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private Site site;
  private String googleMeeting;
  private String title;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private boolean isRemote;
  private boolean attended;
  private Integer patientState;
  private String comments;
  private boolean isExpired;
  private boolean reminderSent;
  private boolean confirmationSent;
  private boolean confirmed;
  private boolean cancelled;
  private String appointmentReason;
  private boolean emergencyAppointment;
  private Integer appointmentNumber;
  private String appointmentStatus;
}
