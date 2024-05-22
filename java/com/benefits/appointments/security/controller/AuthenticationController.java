package com.benefits.appointments.security.controller;

import com.benefits.appointments.models.dto.output.StandardResponse;
import com.benefits.appointments.security.dto.ChangeUserPassReq;
import com.benefits.appointments.security.dto.ChangeUserStatusReq;
import com.benefits.appointments.security.dto.LoginResponse;
import com.benefits.appointments.security.dto.LoginUserDto;
import com.benefits.appointments.security.dto.RegisterUserDto;
import com.benefits.appointments.security.dto.ResetPasswordReq;
import com.benefits.appointments.security.dto.SignUpDto;
import com.benefits.appointments.security.entity.User;
import com.benefits.appointments.security.service.AuthenticationService;
import com.benefits.appointments.security.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

  private final JwtService jwtService;
  private final AuthenticationService authenticationService;

  public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
    this.jwtService = jwtService;
    this.authenticationService = authenticationService;
  }

  @PostMapping("/signup-admin")
  public ResponseEntity<StandardResponse> registerAdmin(@RequestBody RegisterUserDto registerUserDto) {
    authenticationService.registerAdmin(registerUserDto);
    return ResponseEntity.status(201).body(new StandardResponse("User created", "none"));
  }

  @PostMapping("/signup")
  public ResponseEntity<StandardResponse> signup(@Valid @RequestBody SignUpDto signUpDto) {

    authenticationService.signup(signUpDto);
    return ResponseEntity.status(201).body(new StandardResponse("User created", "none"));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<StandardResponse> resetPassword(@Valid @RequestBody ResetPasswordReq resetPasswordReq) {
    authenticationService.resetPassword(resetPasswordReq);
    return ResponseEntity.ok(new StandardResponse("Email sent to the user", "none"));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
    User authenticatedUser = authenticationService.authenticate(loginUserDto);
    String jwtToken = jwtService.generateToken(authenticatedUser);
    LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime(),
        authenticatedUser.getRole().getName(), authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
        authenticatedUser.getStatus(), authenticatedUser.getWorkday(),authenticatedUser.getWorkEmail(),
        authenticatedUser.getPersonalEmail(),authenticatedUser.getSpecialist() != null ? authenticatedUser.getSpecialist().getProfession().getOccupation().getAlias() : null);
    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping(path = "/change-user-status", consumes = "application/json")
  public ResponseEntity<StandardResponse> changeUserStatus(
      @Valid @RequestBody ChangeUserStatusReq changeUserStatusReq) {
    authenticationService.changeUserStatus(changeUserStatusReq);
    return ResponseEntity.ok(new StandardResponse("User status changed", "none"));
  }

  @PostMapping(path = "/change-password", consumes = "application/json")
  public ResponseEntity<StandardResponse> changeUserPassword(@Valid @RequestBody ChangeUserPassReq changeUserPassReq) {
    authenticationService.changeUserPassword(changeUserPassReq);
    return ResponseEntity.ok(new StandardResponse("User password changed", "none"));
  }
}
