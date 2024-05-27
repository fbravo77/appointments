package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.input.UserBatchRegisterInputDTO;
import com.benefits.appointments.models.dto.output.UserBatchRegisterOutputDTO;
import com.benefits.appointments.models.dto.input.UpdateSpecialistLocationInputDTO;
import com.benefits.appointments.models.dto.output.AppointmentOutputDTO;
import com.benefits.appointments.models.dto.output.PatientOutputDTO;
import com.benefits.appointments.models.entities.Account;
import com.benefits.appointments.models.entities.Appointment;
import com.benefits.appointments.models.entities.Patient;
import com.benefits.appointments.models.entities.Site;
import com.benefits.appointments.models.entities.Specialist;
import com.benefits.appointments.models.enums.RoleEnum;
import com.benefits.appointments.models.enums.UserStatus;
import com.benefits.appointments.models.enums.WorkModalityEnum;
import com.benefits.appointments.repositories.AccountRepository;
import com.benefits.appointments.repositories.AppointmentRepository;
import com.benefits.appointments.repositories.PatientRepository;
import com.benefits.appointments.repositories.SiteRepository;
import com.benefits.appointments.repositories.SpecialistRepository;
import com.benefits.appointments.security.entity.Role;
import com.benefits.appointments.security.entity.User;
import com.benefits.appointments.security.repository.RolRepository;
import com.benefits.appointments.security.repository.UserRepository;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  RolRepository roleRepository;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  PatientRepository patientRepository;
  @Autowired
  SiteRepository siteRepository;
  @Autowired
  AppointmentRepository appointmentRepository;

  @Autowired
  SpecialistRepository specialistRepository;

  Mapper mapper = DozerBeanMapperBuilder.buildDefault();
  private static final Logger logger = LogManager.getLogger(AdminService.class);

  public List<PatientOutputDTO> getAllPatients() {
    List<PatientOutputDTO> patientsList = new ArrayList<>();
    List<User> userList = userRepository.findByRole(
        roleRepository.findById(RoleEnum.ROLE_PATIENT.getId())
            .orElseThrow());
    for (User currentUser : userList) {
      PatientOutputDTO patientOutputDTO = mapper.map(currentUser, PatientOutputDTO.class);
      patientOutputDTO.setAccount(currentUser.getPatient().getAccount().getName());
      patientOutputDTO.setAge(currentUser.getPatient().getAge() != null ? currentUser.getPatient().getAge() : 0);
      patientsList.add(patientOutputDTO);
    }
    return patientsList;
  }

  @Transactional
  public List<UserBatchRegisterOutputDTO> uploadPatientsBatch(List<UserBatchRegisterInputDTO> input) {
    try {
      List<UserBatchRegisterOutputDTO> responseList = new ArrayList<>();
      List<String>usersCreatedList = new ArrayList<>();
      Role patientRole = roleRepository.findById(RoleEnum.ROLE_PATIENT.getId()).get();
      Map<String,Integer> usersWorkdayMap = new HashMap<>();
      StringBuilder errorsOnInput = new StringBuilder();
      for(UserBatchRegisterInputDTO currentInput : input) {
        int repeatCounter = usersWorkdayMap.getOrDefault(currentInput.getWorkday(), 0) + 1;
        usersWorkdayMap.put(currentInput.getWorkday(),repeatCounter);
        if(repeatCounter > 1){
          if(errorsOnInput.isEmpty()) errorsOnInput.append("Repeated workdays: ");
          errorsOnInput.append(currentInput.getWorkday()).append(",");
        }
        if(currentInput.getAccountName() == null || currentInput.getAccountName().isEmpty()){
          errorsOnInput.append(" Account empty for Workday: ").append(currentInput.getAccountName());
        }
      }
      if(!errorsOnInput.isEmpty()){
        throw new IllegalArgumentException(errorsOnInput.toString().endsWith(",") ? errorsOnInput.substring(0,errorsOnInput.length()-1) : errorsOnInput.toString());
      }
      List<String> usersInDB = userRepository.findByWorkdayInAndRoleAndIsActive(usersWorkdayMap.keySet(),patientRole,true);

      for(UserBatchRegisterInputDTO currentInput: input) {
        UserBatchRegisterOutputDTO currentRes = new UserBatchRegisterOutputDTO();
        if (usersInDB.contains(currentInput.getWorkday())) {
          usersCreatedList.add(currentInput.getWorkday());
          continue;
        }
        User user = mapper.map(currentInput, User.class);

        user.setRole(patientRole);
        user.setActive(true);
        user.setStatus(UserStatus.UPLOADED_NOT_REGISTERED);

        user = userRepository.save(user);


        Optional<Account> account = accountRepository.findByName(currentInput.getAccountName());
        Optional<Site> site = siteRepository.findByName(currentInput.getSite());

        Patient patient = new Patient(user,
            account.orElseGet(
                () -> accountRepository.save(
                    new Account(currentInput.getAccountName(), currentInput.getAccountName()))),
            currentInput.getPreferredName(),
            currentInput.getAge(),
            false,
            WorkModalityEnum.findByAlias(currentInput.getWorkModality()),
            site.orElseGet(() -> siteRepository.save(new Site(currentInput.getSite(), currentInput.getSite()))));

        patientRepository.save(patient);
        //RESPONSE
        currentRes.setFullName(currentInput.getPreferredName());
        currentRes.setWorkday(currentInput.getWorkday());
        currentRes.setStatus("User created");
        responseList.add(currentRes);
        usersCreatedList.add(currentInput.getWorkday());
      }
      List<User> removedUsers = userRepository.findByWorkdayNotInAndRoleAndIsActive(usersCreatedList, patientRole,
          true);
      for (User currentUser : removedUsers) {
        if (currentUser.getStatus().equals(UserStatus.LOCKED)) {
          continue;
        }
        currentUser.setStatus(UserStatus.LOCKED);
        currentUser.setActive(false);
        UserBatchRegisterOutputDTO currentRes = new UserBatchRegisterOutputDTO();
        currentRes.setStatus("User locked");
        currentRes.setWorkday(currentUser.getWorkday());
        currentRes.setFullName(currentUser.getFirstName());
        responseList.add(currentRes);
        userRepository.save(currentUser);
      }
      return responseList;
    } catch (Exception e) {
      logger.error("Error during uploadPatientsBatch: {}", e.getMessage());
      throw e;
    }
  }

  public List<AppointmentOutputDTO> getPatientsAppointments(String specialistWD) {
    List<User> specialistUser = userRepository.findByWorkdayContainingIgnoreCaseAndRole(specialistWD,
        roleRepository.findById(RoleEnum.ROLE_SPECIALIST.getId())
            .orElseThrow());
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime from = LocalDateTime.of(now.getYear(), Month.JANUARY,1,0,0);
    List<Appointment> appointments = appointmentRepository.findBySpecialistInAndStartDateBetweenOrderByStartDateDesc(specialistUser,now,from);
    List<AppointmentOutputDTO> appointmentOutputDTOList = new ArrayList<>();
    for (Appointment appointment : appointments) {
      AppointmentOutputDTO appointmentDTO = new AppointmentOutputDTO(appointment);
      appointmentOutputDTOList.add(appointmentDTO);
    }
    return appointmentOutputDTOList;
  }

  public void updateSpecialistLocation (UpdateSpecialistLocationInputDTO input){
    Site site = siteRepository.findByName(input.getSite()).orElseThrow(() -> new RuntimeException("Site not found"));
    Specialist specialist =  userRepository.findByWorkday(input.getWorkday()).orElseThrow(() -> new RuntimeException("Specialist not found")).getSpecialist();
    specialist.setCurrentSite(site);
    specialistRepository.save(specialist);
  }
}
