package com.benefits.appointments.security.dto;

import lombok.Data;

@Data
public class RegisterUserDto {

  private String workday;
  private String firstName;
  private String lastName;
  private String personalEmail;
  private String workEmail;
  private String password;
  private Boolean isActive;
  private String contactPhone;
  private String gender;
  private String userRole;
  private String accountName;
  private String profession;
  private String site;

}
