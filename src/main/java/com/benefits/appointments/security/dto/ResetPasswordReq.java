package com.benefits.appointments.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordReq {

  @NotBlank
  private String workday;
}
