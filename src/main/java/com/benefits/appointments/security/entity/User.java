package com.benefits.appointments.security.entity;

import com.benefits.appointments.models.entities.BaseEntity;
import com.benefits.appointments.models.entities.Patient;
import com.benefits.appointments.models.entities.Specialist;
import com.benefits.appointments.models.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

  @Table(name = "users")
  @Entity
  @Data
  @EqualsAndHashCode(callSuper = true)
  public class User extends BaseEntity implements UserDetails {

    @Column(unique = true)
    protected String workday;
    protected String firstName;
    protected String lastName;
    private String password;
    protected String workEmail;

    @Column()
    protected String personalEmail;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    protected Date createdAt;

    @Column(nullable = false)
    protected Boolean isActive;

    protected String contactPhone;
    private String gender;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Patient patient;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Specialist specialist;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName().name());
    return List.of(authority);
  }

  @Override
  public String getUsername() {
    return workday;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return isActive;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isActive;
  }

    @Override
    public String toString() {
      return "User{" +
          "workday='" + workday + '\'' +
          ", firstName='" + firstName + '\'' +
          ", lastName='" + lastName + '\'' +
          ", password='" + password + '\'' +
          ", workEmail='" + workEmail + '\'' +
          ", personalEmail='" + personalEmail + '\'' +
          ", createdAt=" + createdAt +
          ", isActive=" + isActive +
          ", contactPhone='" + contactPhone + '\'' +
          ", gender='" + gender + '\'' +
          ", role=" + role +
          ", status=" + status +
          ", patient=" + patient +
          ", specialist=" + specialist +
          '}';
    }
  }
