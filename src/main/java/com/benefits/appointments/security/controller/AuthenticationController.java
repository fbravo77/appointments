package com.benefits.appointments.security.controller;

import com.benefits.appointments.models.dto.output.StandardResponse;
import com.benefits.appointments.security.dto.ChangeUserPasswordInputDTO;
import com.benefits.appointments.security.dto.ChangeUserStatusInputDTO;
import com.benefits.appointments.security.dto.LoginOutputDTO;
import com.benefits.appointments.security.dto.LoginInputDTO;
import com.benefits.appointments.security.dto.RegisterUserInputDTO;
import com.benefits.appointments.security.dto.ResetPasswordInputDTO;
import com.benefits.appointments.security.dto.PatientSignUpInputDTO;
import com.benefits.appointments.security.entity.User;
import com.benefits.appointments.security.service.AuthenticationService;
import com.benefits.appointments.security.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

  private final JwtService jwtService;
  private final AuthenticationService authenticationService;
  private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
    this.jwtService = jwtService;
    this.authenticationService = authenticationService;
  }

  @PostMapping("/signup-admin")
  public ResponseEntity<StandardResponse> registerAdmin(@RequestBody RegisterUserInputDTO registerUserInputDTO) {
    logger.info("Registering new admin-specialist: {}", registerUserInputDTO.getWorkday());
    authenticationService.registerAdmin(registerUserInputDTO);
    return ResponseEntity.status(201).body(new StandardResponse("User created", "none"));
  }

  @PostMapping("/signup")
  public ResponseEntity<StandardResponse> signup(@Valid @RequestBody PatientSignUpInputDTO patientSignUpInputDTO) {
    logger.info("Registering new user: {}", patientSignUpInputDTO.getWorkday());
    authenticationService.signup(patientSignUpInputDTO);
    return ResponseEntity.status(201).body(new StandardResponse("User created", "none"));
  }

  @PatchMapping("/reset-password")
  public ResponseEntity<StandardResponse> resetPassword(@Valid @RequestBody ResetPasswordInputDTO resetPasswordReq) {
    logger.info("Resetting password for user: {}", resetPasswordReq.getWorkday());
    authenticationService.resetPassword(resetPasswordReq);
    return ResponseEntity.ok(new StandardResponse("Email sent to the user", "none"));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginOutputDTO> authenticate(@Valid @RequestBody LoginInputDTO loginUserDto) {
    logger.info("Authenticating user: {}", loginUserDto.getWorkday());
    User authenticatedUser = authenticationService.authenticate(loginUserDto);
    String jwtToken = jwtService.generateToken(authenticatedUser);
    LoginOutputDTO loginOutputDTO = new LoginOutputDTO(jwtToken, jwtService.getExpirationTime(),
        authenticatedUser.getRole().getName(), authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
        authenticatedUser.getStatus(), authenticatedUser.getWorkday(),authenticatedUser.getWorkEmail(),
        authenticatedUser.getPersonalEmail(),authenticatedUser.getSpecialist() != null ? authenticatedUser.getSpecialist().getProfession().getOccupation().getAlias() : null);
    return ResponseEntity.ok(loginOutputDTO);
  }

  @PatchMapping(path = "/change-user-status", consumes = "application/json")
  public ResponseEntity<StandardResponse> changeUserStatus(
      @Valid @RequestBody ChangeUserStatusInputDTO changeUserStatusInputDTO) {
    logger.info("Changing status for user: {}", changeUserStatusInputDTO.getWorkday());
    authenticationService.changeUserStatus(changeUserStatusInputDTO);
    return ResponseEntity.ok(new StandardResponse("User status changed", "none"));
  }

  @PatchMapping(path = "/change-password", consumes = "application/json")
  public ResponseEntity<StandardResponse> changeUserPassword(@Valid @RequestBody ChangeUserPasswordInputDTO changeUserPasswordInputDTO) {
    logger.info("Changing password for user: {}", changeUserPasswordInputDTO.getWorkday());
    authenticationService.changeUserPassword(changeUserPasswordInputDTO);
    return ResponseEntity.ok(new StandardResponse("User password changed", "none"));
  }
}
