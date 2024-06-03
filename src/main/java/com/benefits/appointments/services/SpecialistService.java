package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.input.UpdateAppointmentInputDTO;
import com.benefits.appointments.models.dto.output.AppointmentOutputDTO;
import com.benefits.appointments.models.dto.output.CalendarEventsOutputDTO;
import com.benefits.appointments.models.dto.output.PatientOutputDTO;
import com.benefits.appointments.models.dto.output.ProfessionOutputDTO;
import com.benefits.appointments.models.dto.output.SpecialistOutputDTO;
import com.benefits.appointments.models.entities.Appointment;
import com.benefits.appointments.models.entities.Profession;
import com.benefits.appointments.models.entities.Specialist;
import com.benefits.appointments.models.enums.ProfessionsEnum;
import com.benefits.appointments.models.enums.RoleEnum;
import com.benefits.appointments.repositories.AppointmentRepository;
import com.benefits.appointments.repositories.ProfessionRepository;
import com.benefits.appointments.repositories.SpecialistRepository;
import com.benefits.appointments.security.entity.User;
import com.benefits.appointments.security.repository.RolRepository;
import com.benefits.appointments.security.repository.UserRepository;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class SpecialistService {
  private final SpecialistRepository specialistRepository;
  private final ProfessionRepository professionRepository;
  private final AppointmentRepository appointmentRepository;
  private final UserRepository userRepository;
  private final RolRepository roleRepository;
  private final CalendarService calendarService;
  private final Mapper mapper = DozerBeanMapperBuilder.buildDefault();
  private static final Logger logger = LogManager.getLogger(SpecialistService.class);

  public SpecialistService(SpecialistRepository specialistRepository, ProfessionRepository professionRepository,
                           AppointmentRepository appointmentRepository, UserRepository userRepository,
                           RolRepository roleRepository, CalendarService calendarService) {
    this.specialistRepository = specialistRepository;
    this.professionRepository = professionRepository;
    this.appointmentRepository = appointmentRepository;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.calendarService = calendarService;
  }
  @Transactional(readOnly = true)
  public List<SpecialistOutputDTO> getSpecialists(String professionName) {
    List<Specialist> specialists = findSpecialistsByProfession(professionName);
    List<SpecialistOutputDTO> specialistOutputDTOList = new ArrayList<>();
    for (Specialist currentSpecialist : specialists) {
      SpecialistOutputDTO specialistResponseDTO = new SpecialistOutputDTO(currentSpecialist.getUser());
      specialistOutputDTOList.add(specialistResponseDTO);
    }
    return specialistOutputDTOList;
  }

  private List<Specialist> findSpecialistsByProfession(String professionName) {
    if (professionName != null) {
      Profession profession = professionRepository.findByOccupation(ProfessionsEnum.valueOf(professionName))
          .orElseThrow(() -> new IllegalArgumentException("Profession not found"));
      return specialistRepository.findByProfession(profession);
    } else {
      return specialistRepository.findAll();
    }
  }

  @Transactional(readOnly = true)
  public List<AppointmentOutputDTO> getAppointments(String specialistWD, String patientWD) {
    List<User> specialists = findUsersByWorkdayAndRole(specialistWD, RoleEnum.ROLE_SPECIALIST);
    List<User> patients = findUsersByWorkdayAndRole(patientWD, RoleEnum.ROLE_PATIENT);
    List<Appointment> appointments = findAppointments(specialists, patients);

    List<AppointmentOutputDTO> appointmentOutputDTOList = new ArrayList<>();
    for (Appointment currentAppointment : appointments) {
      AppointmentOutputDTO appointmentDTO = new AppointmentOutputDTO(currentAppointment);
      appointmentOutputDTOList.add(appointmentDTO);
    }
    return appointmentOutputDTOList;
  }

  private List<User> findUsersByWorkdayAndRole(String workday, RoleEnum role) {
    if (workday != null) {
      return userRepository.findByWorkdayContainingIgnoreCaseAndRole(workday,
          roleRepository.findById(role.getId()).orElseThrow());
    }
    return new ArrayList<>();
  }

  private List<Appointment> findAppointments(List<User> specialists, List<User> patients) {
    LocalDateTime to = LocalDateTime.of(LocalDate.now().getYear(), Month.DECEMBER, 31, 23, 59);
    LocalDateTime from = LocalDateTime.of(LocalDateTime.now().getYear(), Month.JANUARY, 1, 0, 0);

    if (!specialists.isEmpty() && !patients.isEmpty()) {
      return appointmentRepository.findBySpecialistInAndPatientInAndStartDateBetweenOrderByStartDateDesc(specialists, patients, from, to);
    } else if (!specialists.isEmpty()) {
      return appointmentRepository.findBySpecialistInAndStartDateBetweenOrderByStartDateDesc(specialists, from, to);
    } else if (!patients.isEmpty()) {
      return appointmentRepository.findByPatientInAndStartDateBetweenOrderByStartDate(patients, from, to);
    } else {
      return appointmentRepository.findByStartDateBetweenOrderByStartDateDesc(from, to);
    }
  }

  @Transactional(readOnly = true)
  public List<CalendarEventsOutputDTO> getUnavailableDates(String specialistWD) throws GeneralSecurityException, IOException {
    User user = userRepository.findByWorkday(specialistWD)
        .orElseThrow(() -> new NoSuchElementException("Specialist WD not found"));
    return calendarService.getAvailableDates(user.getWorkday());
  }

  @Transactional(readOnly = true)
  public List<PatientOutputDTO> getPatients(String specialistWD) {
    if (specialistWD != null) {
      return appointmentRepository.getAppointmentsWithDetails(specialistWD);
    }
    return new ArrayList<>();
  }

  @Transactional(readOnly = true)
  public List<ProfessionOutputDTO> getProfessions() {
    return specialistRepository.getProfessions();
  }

  @Transactional
  public void updateAppointment(UpdateAppointmentInputDTO updateAppointmentInputDTO) {
    Appointment appointment = appointmentRepository.findById(updateAppointmentInputDTO.getId())
        .orElseThrow(() -> new NoSuchElementException("Appointment not found"));
    if (appointment.isSpecialistUpdated()) {
      throw new RuntimeException("Appointment already updated");
    }
    appointment.updateAppointmentDetails(updateAppointmentInputDTO);
    appointmentRepository.save(appointment);
  }
}
