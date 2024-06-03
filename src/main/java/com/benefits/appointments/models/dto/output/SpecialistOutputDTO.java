package com.benefits.appointments.models.dto.output;

import com.benefits.appointments.models.entities.Specialist;
import com.benefits.appointments.security.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistOutputDTO {

  private String workday;
  private String firstName;
  private String lastName;
  private String contactEmail;
  private String gender;
  private String currentLocation;

  public SpecialistOutputDTO(User user) {
    this.workday = user.getWorkday();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.contactEmail = user.getWorkEmail();
    this.gender = user.getGender();
    this.currentLocation = user.getSpecialist().getCurrentSite().getName();
  }
}
