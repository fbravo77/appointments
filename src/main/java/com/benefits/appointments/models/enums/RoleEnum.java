package com.benefits.appointments.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleEnum {

    ROLE_ADMIN(1), ROLE_SPECIALIST(2), ROLE_PATIENT(3);
    private final Integer id;
}
