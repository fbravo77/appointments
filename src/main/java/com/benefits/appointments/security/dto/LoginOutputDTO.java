package com.benefits.appointments.security.dto;

import com.benefits.appointments.models.enums.RoleEnum;
import com.benefits.appointments.models.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class LoginOutputDTO {
  private String token;
  private long expiresIn;
  private RoleEnum roleEnum;
  private String fullName;
  private UserStatus status;
  private String workday;
  private String workEmail;
  private String personalEmail;
  private String occupation;
}
