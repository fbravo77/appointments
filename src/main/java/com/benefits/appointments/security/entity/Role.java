package com.benefits.appointments.security.entity;

import com.benefits.appointments.models.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.benefits.appointments.models.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ROLES")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoleEnum name;

  @Override
  public String toString() {
    return "Role{" +
        "name=" + name +
        '}';
  }
}
