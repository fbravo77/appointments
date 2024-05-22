package com.benefits.appointments.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUserDto {
  @NotBlank(message = "workday cannot be empty")
  private String workday;
  @NotBlank(message = "password cannot be empty")
  private String password;
}
