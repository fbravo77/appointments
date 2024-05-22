package com.benefits.appointments.models.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterUserBatchDTOReq {

  @NotBlank
  private String workday;
  private String preferredName;
  @NotBlank
  private String firstName;
  private String lastName;
  @NotBlank
  private String personalEmail;
  private String workEmail;
  private String contactPhone;
  @NotBlank
  private String gender;
  @NotBlank
  private String accountName;
  @NotBlank
  private String site;
  @NotNull
  private Integer age;
  private String workModality;
}
