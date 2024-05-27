package com.benefits.appointments.models.dto.output;

import com.benefits.appointments.models.enums.ProfessionsEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfessionOutputDTO {

  Long id;
  ProfessionsEnum occupation;

}
