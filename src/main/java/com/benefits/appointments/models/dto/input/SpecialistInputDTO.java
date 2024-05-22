package com.benefits.appointments.models.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpecialistInputDTO {

  @NotBlank
  private String workday;

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  private int age;

  @Size(min = 7, max = 12)
  private String contactPhone1;

  private String contactPhone2;

  @Email
  private String contactEmail;

  @NotBlank
  @Size(min = 1, max = 1) //M OR F; MALE OR FEMALE
  private String gender;

  private String dateOfBirth;
  private boolean isActive;
  private long occupation;


}
