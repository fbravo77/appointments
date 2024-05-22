package com.benefits.appointments.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProfessionsEnum {
  PSYCHOLOGIST("psychologist"), NUTRITIONIST("nutritionist");

  private final String alias;

  public static ProfessionsEnum findByAlias(String alias) {
    for (ProfessionsEnum value : values()) {
      if (value.getAlias().equals(alias)) {
        return value;
      }
    }
    return null; // Return null if no enum constant with the given alias is found
  }
}
