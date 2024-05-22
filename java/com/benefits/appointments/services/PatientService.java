package com.benefits.appointments.services;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

import com.benefits.appointments.models.dto.input.AppointmentsReqDTO;
import com.benefits.appointments.models.dto.input.ConfirmAppointmentReqDTO;
import com.benefits.appointments.models.dto.output.CalendarEventsResDto;
import com.benefits.appointments.models.dto.output.CalendarEventsResDto.Attendees;
import com.benefits.appointments.models.dto.output.PatientAppointmentsValidationResDTO;
import com.benefits.appointments.models.dto.output.PatientConfirmationResDto;
import com.benefits.appointments.models.dto.output.PatientsResponseDTO;
import com.benefits.appointments.models.dto.output.StandardResponse;
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
import jakarta.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;

@Service
public class PatientService {

  @Autowired
  RolRepository rolRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  AppointmentRepository appointmentRepository;
  @Autowired
  SiteRepository siteRepository;
  @Autowired
  CalendarService calendarService;
  Mapper mapper = DozerBeanMapperBuilder.buildDefault();
  private static final Logger logger = LogManager.getLogger(PatientService.class);

  @Transactional
  public CalendarEventsResDto createAppointment(AppointmentsReqDTO input) {
    try {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      User patientUser = userRepository.findByWorkday(input.getPatientWorkday())
          .orElseThrow(() -> new RuntimeException("patient Workday not found"));
      User specialistUser = userRepository.findByWorkday(input.getSpecialistWorkday())
          .orElseThrow(() -> new RuntimeException("specialist Workday not found"));
      //Check appointment Number
      LocalDateTime to = LocalDateTime.of(LocalDate.now().getYear(),Month.DECEMBER,31,23,59);
      LocalDateTime from = LocalDateTime.of(LocalDateTime.now().getYear(), Month.JANUARY,1,0,0);
      List<Appointment> appointments = appointmentRepository.findBySpecialistAndPatientAndStartDateBetweenOrderByStartDateDesc(specialistUser,patientUser,from,to);
      int appointmentNumber = 1;
      if(!appointments.isEmpty()) {
        appointmentNumber = appointments.get(0).getAppointmentNumber()  != null ? appointments.get(0).getAppointmentNumber() + 1 : 1;
        if(specialistUser.getSpecialist().getProfession().getOccupation().equals(ProfessionsEnum.PSYCHOLOGIST)) {
          if(appointmentNumber > 5 && !patientUser.getPatient().getCanCreateMoreAppointments()){
            throw new RuntimeException("Patient already scheduled the limit of appointments");
          }
        }
      }
      Site site = input.getRemote() ? null : siteRepository.findByName(input.getLocation()).orElseThrow(() -> new RuntimeException("Location not found"));
      Appointment appointment = getAppointment(input, patientUser, specialistUser);
      appointment.setSite(site);
      appointment.setAppointmentNumber(appointmentNumber);
      appointment.setAppointmentStatus("Agendada");
      appointmentRepository.save(appointment);

      return new CalendarEventsResDto(patientUser.getWorkEmail(), input.getStartDate(), input.getEndDate(),
          appointment.getGoogleMeeting(), input.getSummary(), LocalDateTime.now().toString(), patientUser.getPersonalEmail());
    } catch (RuntimeException e) {
      logger.error("Error during createAppointment: {}", e.getMessage());
      throw e;
    }
  }

  private Appointment getAppointment(AppointmentsReqDTO input, User patientUser, User specialistUser) {
    LocalDateTime startDate = ZonedDateTime.parse(input.getStartDate()).toLocalDateTime();
    LocalDateTime endDate = ZonedDateTime.parse(input.getEndDate()).toLocalDateTime();
    Appointment appointment = new Appointment();
    appointment.setPatient(patientUser);
    appointment.setSpecialist(specialistUser);
    //TODO Validate Date
    appointment.setStartDate(startDate);
    appointment.setEndDate(endDate);
    appointment.setIsRemote(input.getRemote());
    appointment.setConfirmationSent(false);
    appointment.setReminderSent(false);

    //CREATE GOOGLE MEETING

      String googleMeet = calendarService.createMeeting(specialistUser.getWorkday(), input, patientUser.getWorkEmail(),
          patientUser.getPersonalEmail(),specialistUser.getWorkEmail(),specialistUser.getPersonalEmail());

    appointment.setTittle(input.getTitle());
    appointment.setGoogleMeeting(googleMeet);
    appointmentRepository.save(appointment);
    return appointment;
  }

  public List<PatientsResponseDTO> getPatientsByWorkday(String workday) {
    List<User> userList = userRepository.findByWorkdayContainingIgnoreCaseAndRole(workday,
        rolRepository.findById(RoleEnum.ROLE_PATIENT.getId()).orElseThrow());
    List<PatientsResponseDTO> patientsList = new ArrayList<>();
    for (User currentUser : userList) {
      PatientsResponseDTO patientsResponseDTO = mapper.map(currentUser, PatientsResponseDTO.class);
      patientsResponseDTO.setAccount(currentUser.getPatient().getAccount().getName());
      patientsResponseDTO.setAge(currentUser.getPatient().getAge());
      patientsList.add(patientsResponseDTO);
    }
    return patientsList;
  }

  public PatientAppointmentsValidationResDTO getAppointmentsValidation(String patientWD) {
    User patientUser = userRepository.findByWorkdayAndRole(patientWD,
            rolRepository.findById(RoleEnum.ROLE_PATIENT.getId()).get())
        .orElseThrow(() -> new IllegalArgumentException("Patient WD not found"));

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
      if (currentAppointment.getCancelled() != null && currentAppointment.getCancelled()) {
        cancelledAppointments++;
      }
      if (currentAppointment.getStartDate().isBefore(now) || currentAppointment.getStartDate().getMonth()
          .equals(now.getMonth())) {
        appointmentsCount++;
      }
    }
    return new PatientAppointmentsValidationResDTO(appointmentsCount, cancelledAppointments, scheduledForCurrentMonth);
  }

  @Transactional
  public void confirmAppointment(ConfirmAppointmentReqDTO input) {
    try {
      Optional<Appointment> appointment = appointmentRepository.findById(input.getAppointmentId());
      if(appointment.isEmpty()) throw new RuntimeException("No Appointment with that ID");
      if(appointment.get().getConfirmed() != null || appointment.get().getCancelled() != null)
          throw new RuntimeException("Appointment already confirmed");

      if(input.getCanceled()){
        appointment.get().setCancelled(true);
        appointment.get().setConfirmed(false);
        appointment.get().setAppointmentStatus("Cancelada");
        appointment.get().setComments("Paciente cancelo debido a: " + input.getNotes());
      }
      else {
        appointment.get().setConfirmed(true);
        appointment.get().setCancelled(false);
        appointment.get().setAppointmentStatus("Confirmada");
      }
      appointmentRepository.save(appointment.get());

    } catch (RuntimeException e) {
      logger.error("Error during createAppointment: {}", e.getMessage());
      throw e;
    }
  }

  public PatientConfirmationResDto findAppointmentById(String appointmentId) {
    Optional<Appointment> appointment = appointmentRepository.findById(Long.valueOf(appointmentId));
    if(appointment.isEmpty()) throw new RuntimeException("Workday id not found");
    return new PatientConfirmationResDto(appointment.get());
  }
}
