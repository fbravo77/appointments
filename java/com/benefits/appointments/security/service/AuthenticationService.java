package com.benefits.appointments.security.service;

import com.benefits.appointments.models.entities.Profession;
import com.benefits.appointments.models.entities.Specialist;
import com.benefits.appointments.models.enums.ProfessionsEnum;
import com.benefits.appointments.models.enums.RoleEnum;
import com.benefits.appointments.models.enums.UserStatus;
import com.benefits.appointments.repositories.AccountRepository;
import com.benefits.appointments.repositories.PatientRepository;
import com.benefits.appointments.repositories.ProfessionRepository;
import com.benefits.appointments.repositories.SpecialistRepository;
import com.benefits.appointments.security.dto.ChangeUserPassReq;
import com.benefits.appointments.security.dto.ChangeUserStatusReq;
import com.benefits.appointments.security.dto.LoginUserDto;
import com.benefits.appointments.security.dto.RegisterUserDto;
import com.benefits.appointments.security.dto.ResetPasswordReq;
import com.benefits.appointments.security.dto.SignUpDto;
import com.benefits.appointments.security.entity.Role;
import com.benefits.appointments.security.entity.User;
import com.benefits.appointments.security.repository.RolRepository;
import com.benefits.appointments.security.repository.UserRepository;
import com.benefits.appointments.services.EmailService;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  RolRepository rolRepository;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  PatientRepository patientRepository;
  @Autowired
  ProfessionRepository professionRepository;
  @Autowired
  SpecialistRepository specialistRepository;
  @Autowired
  EmailService emailService;

  Mapper mapper = DozerBeanMapperBuilder.buildDefault();
  private static final Logger logger = LogManager.getLogger(AuthenticationService.class);
  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  public AuthenticationService(
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder
  ) {
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void registerAdmin(RegisterUserDto input) {
    try {

      User user = mapper.map(input, User.class);
      Role roleEnum = rolRepository.findByName(RoleEnum.valueOf(input.getUserRole())).orElseThrow(() ->new IllegalArgumentException("Invalid Role"));
      user.setRole(roleEnum);
      user.setPassword(passwordEncoder.encode(input.getPassword()));
      user.setStatus(UserStatus.PASSWORD_RESET);
      user = userRepository.save(user);

      if(roleEnum.getName().equals(RoleEnum.ROLE_SPECIALIST)){
        if(input.getProfession() == null || input.getProfession().isEmpty()) throw new IllegalArgumentException("Profession name is required for specialists");
        Optional<Profession> profession = professionRepository.findByOccupation(ProfessionsEnum.valueOf(input.getProfession()));
        specialistRepository.save(new Specialist(user,profession.orElseThrow(),null));
      }
    }
    catch(Exception e){
      logger.error("Error during signup: {}", e.getMessage());
      throw e;
    }
  }

  @Transactional
  public void signup(SignUpDto input) {
    try {
      User user = userRepository.findByWorkday(input.getWorkday()).orElseThrow(() ->new NoSuchElementException ("No user found for that workday"));
      user.setPassword(passwordEncoder.encode(input.getPassword()));
      user.setFirstName(input.getFirstName());
      user.setLastName(input.getLastName());
      user.setStatus(UserStatus.VERIFIED);
      user.setContactPhone(input.getPhone());
      user.setPersonalEmail(input.getEmail());
      userRepository.save(user);
    }
    catch(Exception e){
      logger.error("Error during signup: {}", e.getMessage());
      throw e;
    }
  }

  public User authenticate(LoginUserDto input) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            input.getWorkday(),
            input.getPassword()
        )
    );

    return userRepository.findByWorkday(input.getWorkday())
        .orElseThrow(() ->new NoSuchElementException ("No user found for that workday"));
  }

  public void resetPassword(ResetPasswordReq input) {
    try{
    User user = userRepository.findByWorkday(input.getWorkday()).orElseThrow(() ->new NoSuchElementException ("No user found for that workday"));
    String newPassword = generatePassword(8);
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setStatus(UserStatus.PASSWORD_RESET);
    userRepository.save(user);
    //SEND EMAIL
    String[] to = {user.getPersonalEmail(),user.getWorkEmail()};
    String subject = "password reset for Telus Wellness Appointments--TEST";
    String body = "<h1>This is a test email</h1> please enter this temp password to the wellness Appointments app <b>" + newPassword + "</b>";
    emailService.sendEmail(to,subject,body);
    }catch (MessagingException e){
      logger.error("Exception Error: " + e);
    }
  }

  public void changeUserStatus(ChangeUserStatusReq input) {
    User user = userRepository.findByWorkday(input.getWorkday()).orElseThrow(() ->new NoSuchElementException ("No user found for that workday"));
    user.setIsActive(input.isActive());
    user.setStatus(input.isActive() ? UserStatus.VERIFIED : UserStatus.LOCKED);
    userRepository.save(user);
  }

  public void changeUserPassword(ChangeUserPassReq input) {
    User user = userRepository.findByWorkday(input.getWorkday()).orElseThrow(() ->new NoSuchElementException ("No user found for that workday"));
    user.setPassword(passwordEncoder.encode(input.getPassword()));
    user.setStatus(UserStatus.VERIFIED);
    userRepository.save(user);
  }
  public static String generatePassword(int length) {
    final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    return new Random().ints(length, 0, CHARACTERS.length())
        .mapToObj(CHARACTERS::charAt)
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
  }

}
