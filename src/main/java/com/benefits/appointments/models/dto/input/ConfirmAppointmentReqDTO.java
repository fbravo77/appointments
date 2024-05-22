package com.benefits.appointments.models.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmAppointmentReqDTO {

  @NotNull
  private Long appointmentId;
  private String notes;
  @NotNull
  private Boolean canceled;
}
