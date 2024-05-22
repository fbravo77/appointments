package com.benefits.appointments.models.entities;

import com.benefits.appointments.models.enums.ProfessionsEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Professions")
public class Profession extends BaseEntity{

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProfessionsEnum occupation;

}
