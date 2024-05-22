package com.benefits.appointments.models.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentsReqDTO {

  @NotBlank
  private String patientWorkday;
  @NotBlank
  private String startDate;
  @NotBlank
  private String endDate;
  @NotBlank
  private String title;
  @NotBlank
  private String specialistWorkday;
  @NotNull
  private Boolean remote;
  private String summary;
  private String location;
  private String description;

}
