package com.benefits.appointments.repositories;

import com.benefits.appointments.models.entities.Profession;
import com.benefits.appointments.models.enums.ProfessionsEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionRepository extends JpaRepository<Profession, Long> {
  Optional<Profession> findByOccupation(ProfessionsEnum occupation);
}
