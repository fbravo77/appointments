package com.benefits.appointments.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeUserPassReq {

  @NotBlank
  private String workday;

  @NotBlank
  @Size(min = 7)
  private String password;
}
