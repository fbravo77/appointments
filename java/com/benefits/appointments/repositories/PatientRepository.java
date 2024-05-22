package com.benefits.appointments.repositories;

import com.benefits.appointments.models.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {}
