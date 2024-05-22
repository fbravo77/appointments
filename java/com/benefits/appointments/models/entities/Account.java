package com.benefits.appointments.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Accounts")
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity{

  @Column(unique=true)
  private String name;
  @Column(unique = true)
  private String location;
}
