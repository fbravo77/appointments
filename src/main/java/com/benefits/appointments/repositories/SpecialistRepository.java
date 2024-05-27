package com.benefits.appointments.repositories;

import com.benefits.appointments.models.dto.output.ProfessionOutputDTO;
import com.benefits.appointments.models.entities.Profession;
import com.benefits.appointments.models.entities.Specialist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Long> {

  List<Specialist> findByProfession(Profession profession);

  @Query("SELECT NEW com.benefits.appointments.models.dto.output.ProfessionOutputDTO(pf.id, pf.occupation) FROM Profession pf")
  List<ProfessionOutputDTO> getProfessions();

}
