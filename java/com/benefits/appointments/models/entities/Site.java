package com.benefits.appointments.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Sites")
@AllArgsConstructor
@NoArgsConstructor
public class Site extends BaseEntity{

  @Column(unique = true)
  private String name;
  @Column(unique = true)
  private String location;
}
