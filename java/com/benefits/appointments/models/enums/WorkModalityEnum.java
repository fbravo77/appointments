package com.benefits.appointments.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkModalityEnum {

  ON_SITE("Onsite"), REMOTE("Remote"), COMBINED("Combined");

  private final String alias;

  public static WorkModalityEnum findByAlias(String alias) {
    for (WorkModalityEnum value : values()) {
      if (value.getAlias().equals(alias)) {
        return value;
      }
    }
    return null; // Return null if no enum constant with the given alias is found
  }
}
