package com.benefits.appointments.controllers;

import com.benefits.appointments.models.dto.input.UserBatchRegisterInputDTO;
import com.benefits.appointments.models.dto.output.UserBatchRegisterOutputDTO;
import com.benefits.appointments.models.dto.input.UpdateSpecialistLocationInputDTO;
import com.benefits.appointments.models.dto.output.AppointmentOutputDTO;
import com.benefits.appointments.models.dto.output.PatientOutputDTO;
import com.benefits.appointments.models.dto.output.StandardResponse;
import com.benefits.appointments.services.AdminService;
import com.benefits.appointments.services.SpecialistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin", produces = "application/json")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;
  private final SpecialistService specialistService;
  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
  @GetMapping("/patients")
  public ResponseEntity<List<PatientOutputDTO>> getAllPatients() {
    logger.info("Fetching all patients");
    List<PatientOutputDTO> patients = adminService.getAllPatients();
    return ResponseEntity.ok(patients);
  }

  @PostMapping("/patients/upload-batch")
  public ResponseEntity<List<UserBatchRegisterOutputDTO>> uploadPatientsBatch(
      @RequestBody List<UserBatchRegisterInputDTO> registerUserDto) {
    logger.info("Uploading patients batch");
    List<UserBatchRegisterOutputDTO> response = adminService.uploadPatientsBatch(registerUserDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/specialists/location")
  public ResponseEntity<StandardResponse> updateSpecialistLocation(@RequestBody UpdateSpecialistLocationInputDTO input) {
    logger.info("Updating specialist location");
    adminService.updateSpecialistLocation(input);
    return ResponseEntity.ok(new StandardResponse("location updated", "none"));
  }

  @GetMapping("/appointments")
  public ResponseEntity<List<AppointmentOutputDTO>> getAppointments(
      @RequestParam(name = "specialist-wd", required = false) String specialistWD,
      @RequestParam(name = "patient-wd", required = false) String patientWD) {
    logger.info("Fetching appointments for specialistId={} and patientId={}", specialistWD, patientWD);
    List<AppointmentOutputDTO> appointments = specialistService.getAppointments(specialistWD, patientWD);
    return ResponseEntity.ok(appointments);
  }
}
