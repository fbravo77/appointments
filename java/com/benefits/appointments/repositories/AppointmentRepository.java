package com.benefits.appointments.repositories;

import com.benefits.appointments.models.dto.output.PatientsResponseDTO;
import com.benefits.appointments.models.entities.Appointment;
import com.benefits.appointments.security.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  List<Appointment> findBySpecialistInAndStartDateBetweenOrderByStartDateDesc(List<User> specialist,LocalDateTime to, LocalDateTime fr);

  List<Appointment> findBySpecialistAndPatientAndStartDateBetweenOrderByStartDateDesc(User specialist,User patient,LocalDateTime to, LocalDateTime fr);

  List<Appointment> findBySpecialistInAndPatientInAndStartDateBetweenOrderByStartDateDesc(List<User> specialist, List<User> patient,LocalDateTime to, LocalDateTime fr);

  List<Appointment> findByPatientInAndStartDateBetweenOrderByStartDate(List<User> patient,LocalDateTime to, LocalDateTime fr);

  List<Appointment> findByStartDateBetweenOrderByStartDateDesc(LocalDateTime to, LocalDateTime fr);

  List<Appointment> findByStartDateBetweenAndReminderSent(LocalDateTime to, LocalDateTime from, Boolean reminderSent);

  List<Appointment> findByStartDateBetweenAndConfirmationSent(LocalDateTime to, LocalDateTime from, Boolean confirmationSent);

  List<Appointment> findByStartDateBetweenAndPatient (LocalDateTime to, LocalDateTime from, User patientUser);

  @Query("SELECT NEW com.benefits.appointments.models.dto.output.PatientsResponseDTO(up.firstName, up.workEmail, COUNT(app.attended) AS appointments, " +
      "up.workday, up.lastName, up.contactPhone, up.personalEmail, " +
      "up.gender, up.isActive, up.status, pat.age, ac.name, us.firstName) " +
      "FROM Appointment app " +
      "INNER JOIN app.patient up " +
      "INNER JOIN up.patient pat " +
      "INNER JOIN app.specialist us " +
      "INNER JOIN pat.account ac " +
      "GROUP BY up.id, up.firstName, up.workEmail, up.workday, up.lastName, " +
      "up.contactPhone, up.personalEmail, up.gender, up.isActive, up.status, " +
      "pat.age, ac.name, app.specialist.id, us.firstName, us.workday " +
      "HAVING us.workday = :specialistWorkday")
  List<PatientsResponseDTO> getAppointmentsWithDetails(@Param("specialistWorkday") String specialistWorkday);

}
