package com.benefits.appointments.models.entities;

import com.benefits.appointments.security.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Specialists")
@AllArgsConstructor
@NoArgsConstructor
public class Specialist extends BaseEntity {

  @JsonIgnore
  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @ManyToOne
  private Profession profession;

  @JsonIgnore
  @ManyToOne
  private Site currentSite;

  @Override
  public String toString() {
    return "{" +
        ", profession=" + profession.getOccupation() +
        ", site=" + currentSite.getName() +
        '}';
  }
}
