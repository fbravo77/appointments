package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.input.AppUpdateReqDTO;
import com.benefits.appointments.models.dto.output.AppointmentsResDto;
import com.benefits.appointments.models.dto.output.CalendarEventsResDto;
import com.benefits.appointments.models.dto.output.PatientsResponseDTO;
import com.benefits.appointments.models.dto.output.ProfessionsOutDTO;
import com.benefits.appointments.models.dto.output.SpecialistOutDTO;
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
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecialistService {

  @Autowired
  SpecialistRepository specialistRepository;
  @Autowired
  ProfessionRepository professionRepository;
  @Autowired
  AppointmentRepository appointmentRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  RolRepository roleRepository;
  @Autowired
  CalendarService calendarService;

  Mapper mapper = DozerBeanMapperBuilder.buildDefault();
  private static final Logger logger = LogManager.getLogger(SpecialistService.class);

  public List<SpecialistOutDTO> getSpecialists(String professionName) {
    List<Specialist> specialists;
    if (professionName != null) {
      Profession profession = professionRepository.findByOccupation(ProfessionsEnum.valueOf(professionName))
          .orElseThrow(() -> new IllegalArgumentException("Profession not found"));
      specialists = specialistRepository.findByProfession(profession);
    } else {
      specialists = specialistRepository.findAll();
    }
    List<SpecialistOutDTO> specialistOutDTOList = new ArrayList<>();
    for (Specialist currentSpecialist : specialists) {
      SpecialistOutDTO specialistResponseDTO = new SpecialistOutDTO();
      specialistResponseDTO.setContactEmail(currentSpecialist.getUser().getWorkEmail());
      specialistResponseDTO.setGender(currentSpecialist.getUser().getGender());
      specialistResponseDTO.setFirstName(currentSpecialist.getUser().getFirstName());
      specialistResponseDTO.setLastName(currentSpecialist.getUser().getLastName());
      specialistResponseDTO.setWorkday(currentSpecialist.getUser().getWorkday());
      specialistResponseDTO.setCurrentLocation(currentSpecialist.getCurrentSite().getName());
      specialistOutDTOList.add(specialistResponseDTO);
    }
    return specialistOutDTOList;
  }


  public List<AppointmentsResDto> getAppointments(String specialistWD, String patientWD) {
    List<SpecialistOutDTO> specialistOutDTOList = new ArrayList<>();
    List<User> specialist = null, patient = null;
    if (specialistWD != null) {
      specialist = userRepository.findByWorkdayContainingIgnoreCaseAndRole(specialistWD,
              roleRepository.findById(RoleEnum.ROLE_SPECIALIST.getId()).orElseThrow());
    }
    if(patientWD != null){
      patient = userRepository.findByWorkdayContainingIgnoreCaseAndRole(patientWD,
              roleRepository.findById(RoleEnum.ROLE_PATIENT.getId()).orElseThrow());
    }
    List<Appointment> appointments = new ArrayList<>();
    LocalDateTime to = LocalDateTime.of(LocalDate.now().getYear(),Month.DECEMBER,31,23,59);
    LocalDateTime from = LocalDateTime.of(LocalDateTime.now().getYear(), Month.JANUARY,1,0,0);


    if(specialist != null && !specialist.isEmpty() && patient != null && !patient.isEmpty()){
      appointments = appointmentRepository.findBySpecialistInAndPatientInAndStartDateBetweenOrderByStartDateDesc(specialist,patient,from,to);
    }
    else if (specialist != null && !specialist.isEmpty() && patientWD == null) {
      appointments = appointmentRepository.findBySpecialistInAndStartDateBetweenOrderByStartDateDesc(specialist,from,to);
    }
    else if(patient != null && !patient.isEmpty() && specialistWD == null) {
      appointments = appointmentRepository.findByPatientInAndStartDateBetweenOrderByStartDate(patient,from,to);
    }
    else if (patientWD == null && specialistWD == null) {
      appointments = appointmentRepository.findByStartDateBetweenOrderByStartDateDesc(from,to);
    }

    List<AppointmentsResDto> appointmentsResDtoList = new ArrayList<>();
    for (Appointment currentAppointment : appointments) {
      AppointmentsResDto appointmentDTO = new AppointmentsResDto(currentAppointment);
      appointmentsResDtoList.add(appointmentDTO);
    }
    return appointmentsResDtoList;
  }

  public List<CalendarEventsResDto> getUnavailableDates(String specialistWD)
      throws GeneralSecurityException, IOException {
    User user = userRepository.findByWorkday(specialistWD)
        .orElseThrow(() -> new IllegalArgumentException("Specialist WD not found"));
    return calendarService.getAvailableDates(user.getFirstName() + user.getLastName());
  }

  public List<PatientsResponseDTO> getPatients(String specialistWD) {
    List<PatientsResponseDTO> patientsResponseDTOS = new ArrayList<>();
    if (specialistWD != null) {
        patientsResponseDTOS = appointmentRepository.getAppointmentsWithDetails(specialistWD);
    }
    return patientsResponseDTOS;
  }

  public List<ProfessionsOutDTO> getProfessions() {
    return specialistRepository.getProfessions();
  }

  public void updateAppointment(AppUpdateReqDTO appUpdateReqDTO) {
    Appointment appointment = appointmentRepository.findById(appUpdateReqDTO.getId())
        .orElseThrow(() -> new RuntimeException("Appointment not found"));
    if (appointment.getAttended() != null) {
      throw new RuntimeException("Appointment already updated");
    }
    appointment.setAttended(appUpdateReqDTO.getAttended());
    appointment.setComments(appUpdateReqDTO.getComments());
    appointment.setPatientState(appUpdateReqDTO.getPatientState());
    appointment.setEmergencyAppointment(appUpdateReqDTO.getEmergencyAppointment());
    appointment.setAppointmentReason(appUpdateReqDTO.getAppointmentReason());
    appointmentRepository.save(appointment);
  }
}
