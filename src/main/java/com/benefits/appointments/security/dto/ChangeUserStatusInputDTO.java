package com.benefits.appointments.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ChangeUserStatusInputDTO {

  @NotBlank
  private String workday;
  @NotNull
  private boolean active;

}
