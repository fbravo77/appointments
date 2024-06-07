package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.input.AppointmentConfirmationInputDTO;
import com.benefits.appointments.models.dto.input.CreateAppointmentInputDTO;
import com.benefits.appointments.models.dto.output.AppointmentConfirmationOutputDTO;
import com.benefits.appointments.models.dto.output.CalendarEventsOutputDTO;
import com.benefits.appointments.models.dto.output.PatientAppointmentsDetailOutputDTO;
import com.benefits.appointments.models.dto.output.PatientOutputDTO;
import com.benefits.appointments.models.entities.Appointment;
import com.benefits.appointments.models.entities.Site;
import com.benefits.appointments.models.enums.ProfessionsEnum;
import com.benefits.appointments.models.enums.RoleEnum;
import com.benefits.appointments.repositories.AppointmentRepository;
import com.benefits.appointments.repositories.SiteRepository;
import com.benefits.appointments.security.entity.User;
import com.benefits.appointments.security.repository.RolRepository;
import com.benefits.appointments.security.repository.UserRepository;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

  private final RolRepository rolRepository;
  private final UserRepository userRepository;
  private final AppointmentRepository appointmentRepository;
  private final SiteRepository siteRepository;
  private final CalendarService calendarService;
  private final Mapper mapper = DozerBeanMapperBuilder.buildDefault();
  private static final Logger logger = LogManager.getLogger(PatientService.class);

  @Transactional
  public CalendarEventsOutputDTO createAppointment(CreateAppointmentInputDTO input) {
      User patientUser = userRepository.findByWorkday(input.getPatientWorkday())
          .orElseThrow(() -> new UsernameNotFoundException("patient Workday not found"));
      User specialistUser = userRepository.findByWorkday(input.getSpecialistWorkday())
          .orElseThrow(() -> new UsernameNotFoundException("specialist Workday not found"));

      // Check appointment Number
      LocalDateTime to = LocalDateTime.of(LocalDate.now().getYear(), Month.DECEMBER, 31, 23, 59);
      LocalDateTime from = LocalDateTime.of(LocalDateTime.now().getYear(), Month.JANUARY, 1, 0, 0);
      List<Appointment> appointments = appointmentRepository.findBySpecialistAndPatientAndStartDateBetweenOrderByStartDateDesc(
          specialistUser, patientUser, from, to);
      int appointmentNumber = getAppointmentNumber(appointments, specialistUser, patientUser);

      Site site = input.getRemote() ? null : siteRepository.findByName(input.getLocation())
          .orElseThrow(() -> new NoSuchElementException("Location not found"));
      Appointment appointment = getAppointment(input, patientUser, specialistUser);
      appointment.setSite(site);
      appointment.setAppointmentNumber(appointmentNumber);
      appointment.setAppointmentStatus("Agendada");
      appointmentRepository.save(appointment);
      return new CalendarEventsOutputDTO(patientUser.getWorkEmail(), input.getStartDate(), input.getEndDate(),
          appointment.getGoogleMeeting(), input.getSummary(), LocalDateTime.now().toString(), patientUser.getPersonalEmail());
  }

  private static int getAppointmentNumber(List<Appointment> appointments, User specialistUser, User patientUser) {
    int appointmentNumber = 1;
    if (!appointments.isEmpty()) {
      appointmentNumber =
          appointments.get(0).getAppointmentNumber() != null ? appointments.get(0).getAppointmentNumber() + 1 : 1;
      if (specialistUser.getSpecialist().getProfession().getOccupation().equals(ProfessionsEnum.PSYCHOLOGIST)) {
        if (appointmentNumber > 5 && !patientUser.getPatient().isCanCreateMoreAppointments()) {
          throw new RuntimeException("Patient already scheduled the limit of appointments");
        }
      }
    }
    return appointmentNumber;
  }

  private Appointment getAppointment(CreateAppointmentInputDTO input, User patientUser, User specialistUser) {
    LocalDateTime startDate = ZonedDateTime.parse(input.getStartDate()).toLocalDateTime();
    LocalDateTime endDate = ZonedDateTime.parse(input.getEndDate()).toLocalDateTime();
    Appointment appointment = new Appointment();
    appointment.setPatient(patientUser);
    appointment.setSpecialist(specialistUser);
    // TODO: Validate Date
    appointment.setStartDate(startDate);
    appointment.setEndDate(endDate);
    appointment.setRemote(input.getRemote());
    appointment.setComments(input.getDescription());

    // CREATE GOOGLE MEETING
    String googleMeet = calendarService.createMeeting(specialistUser.getWorkday(), input, patientUser.getWorkEmail(),
        patientUser.getPersonalEmail(), specialistUser.getWorkEmail(), specialistUser.getPersonalEmail());

    appointment.setTitle(input.getTitle());
    appointment.setGoogleMeeting(googleMeet);
    appointmentRepository.save(appointment);
    return appointment;
  }

  @Transactional(readOnly = true)
  public List<PatientOutputDTO> getPatientsByWorkday(String workday) {
    List<User> userList = userRepository.findByWorkdayContainingIgnoreCaseAndRole(workday,
        rolRepository.findById(RoleEnum.ROLE_PATIENT.getId()).orElseThrow());

    return userList.stream().map(user -> {
      PatientOutputDTO patient = mapper.map(user, PatientOutputDTO.class);
      patient.setAccount(user.getPatient().getAccount().getName());
      patient.setAge(user.getPatient().getAge());
      return patient;
    }).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PatientAppointmentsDetailOutputDTO getAppointmentsValidation(String patientWD) {
    User patientUser = userRepository.findByWorkdayAndRole(patientWD,
            rolRepository.findById(RoleEnum.ROLE_PATIENT.getId()).get())
        .orElseThrow(() -> new NoSuchElementException("Patient WD not found"));

    List<Appointment> appointments = appointmentRepository.findByStartDateBetweenAndPatient(
        LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0), LocalDateTime.of(2024, Month.DECEMBER, 1, 23, 59), patientUser);
    int appointmentsCount = 0;
    int cancelledAppointments = 0;
    boolean scheduledForCurrentMonth = false;
    LocalDateTime now = LocalDateTime.now();
    for (Appointment currentAppointment : appointments) {
      if (currentAppointment.getStartDate().getMonth().equals(now.getMonth())) {
        scheduledForCurrentMonth = true;
      }
      if (currentAppointment.isCancelled()) {
        cancelledAppointments++;
      }
      if (currentAppointment.getStartDate().isBefore(now) || currentAppointment.getStartDate().getMonth().equals(now.getMonth())) {
        appointmentsCount++;
      }
    }
    return new PatientAppointmentsDetailOutputDTO(appointmentsCount, cancelledAppointments, scheduledForCurrentMonth);
  }

  @Transactional
  public void confirmAppointment(AppointmentConfirmationInputDTO input) {

      Optional<Appointment> appointment = appointmentRepository.findById(input.getAppointmentId());
      if (appointment.isEmpty()) throw new RuntimeException("No Appointment with that ID");
      if (appointment.get().isConfirmed() || !appointment.get().isCancelled())
        throw new RuntimeException("Appointment already confirmed");

      if (input.getCanceled()) {
        appointment.get().setCancelled(true);
        appointment.get().setConfirmed(false);
        appointment.get().setAppointmentStatus("Cancelada");
        appointment.get().setComments("Paciente cancelo debido a: " + input.getNotes());
      } else {
        appointment.get().setConfirmed(true);
        appointment.get().setCancelled(false);
        appointment.get().setAppointmentStatus("Confirmada");
      }
      appointmentRepository.save(appointment.get());

  }

  @Transactional(readOnly = true)
  public AppointmentConfirmationOutputDTO findAppointmentById(String appointmentId) {
    Optional<Appointment> appointment = appointmentRepository.findById(Long.valueOf(appointmentId));
    if (appointment.isEmpty()) throw new RuntimeException("Workday id not found");
    return new AppointmentConfirmationOutputDTO(appointment.get());
  }
}
