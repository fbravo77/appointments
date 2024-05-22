package com.benefits.appointments.models.entities;

import com.benefits.appointments.models.enums.WorkModalityEnum;
import com.benefits.appointments.security.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "Patients")
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseEntity {

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @ManyToOne
  private Account account;

  private String preferredName;
  private Integer age;

  private Boolean canCreateMoreAppointments = false;

  @Enumerated(EnumType.STRING)
  private WorkModalityEnum workModality;

  @ManyToOne
  private Site site;

  @Override
  public String toString() {
    return "Patient{" +
        ", account=" + account.getName() +
        ", preferredName='" + preferredName + '\'' +
        ", age=" + age +
        ", workModality=" + workModality +
        ", site=" + site.getName() +
        '}';
  }
}
