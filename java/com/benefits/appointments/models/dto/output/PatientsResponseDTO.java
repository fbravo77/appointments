package com.benefits.appointments.models.dto.output;

import com.benefits.appointments.models.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientsResponseDTO {

  private String workday;
  private String firstName;
  private String lastName;
  private int age;
  private String contactPhone;
  private String workEmail;
  private String personalEmail;
  private String gender;
  private Boolean isActive;
  private String account;
  private UserStatus status;
  private Long appointments;

  public PatientsResponseDTO(String firstName, String workEmail, Long appointments,
                             String workday, String lastName, String contactPhone,
                             String personalEmail, String gender, boolean isActive,
                             UserStatus status, int age, String accountName, String specialistFirstName) {
    this.firstName = firstName;
    this.workEmail = workEmail;
    this.appointments = appointments;
    this.workday = workday;
    this.lastName = lastName;
    this.contactPhone = contactPhone;
    this.personalEmail = personalEmail;
    this.gender = gender;
    this.isActive = isActive;
    this.status = status; // Assuming UserStatus is an enum
    this.age = age;
    this.account = accountName;
  }
}
