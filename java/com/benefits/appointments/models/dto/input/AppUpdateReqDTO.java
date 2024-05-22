package com.benefits.appointments.models.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class AppUpdateReqDTO {

  @NotNull
  private Long id;
  @NotBlank
  private String comments;
  @NotNull
  private Boolean attended;
  @NotNull
  @Range(min=1 , max=3)
  private Integer patientState;
  @NotNull
  private Boolean emergencyAppointment;
  @NotNull
  private String appointmentReason;

}
