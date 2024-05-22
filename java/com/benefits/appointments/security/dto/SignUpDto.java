package com.benefits.appointments.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpDto {

  @NotBlank
  private String workday;
  @NotBlank
  private String firstName;
  @NotBlank
  private String lastName;
  @NotBlank
  @Size(min = 7)
  private String password;
  @NotBlank
  private String phone;
  @Email
  private String email;
}
