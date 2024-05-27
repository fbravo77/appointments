package com.benefits.appointments.models.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentConfirmationInputDTO {

  @NotNull
  private Long appointmentId;
  private String notes;
  @NotNull
  private Boolean canceled;
}
