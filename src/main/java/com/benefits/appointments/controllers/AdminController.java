package com.benefits.appointments.controllers;

import com.benefits.appointments.models.dto.input.RegisterUserBatchDTOReq;
import com.benefits.appointments.models.dto.input.RegisterUserBatchResDto;
import com.benefits.appointments.models.dto.input.SpecialistLocationReqDto;
import com.benefits.appointments.models.dto.output.AppointmentsResDto;
import com.benefits.appointments.models.dto.output.PatientsResponseDTO;
import com.benefits.appointments.models.dto.output.StandardResponse;
import com.benefits.appointments.services.AdminService;
import com.benefits.appointments.services.SpecialistService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin", produces = "application/json")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

  @Autowired
  AdminService adminService;
  @Autowired
  SpecialistService specialistService;

  @GetMapping("/patients")
  public ResponseEntity<List<PatientsResponseDTO>> getAllPatients() {
    return ResponseEntity.ok(adminService.getAllPatients());
  }

  @PostMapping("/patients/upload-batch")
  public ResponseEntity<List<RegisterUserBatchResDto>> uploadPatientsBatch(
      @RequestBody List<RegisterUserBatchDTOReq> registerUserDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(adminService.uploadPatientsBatch(registerUserDto));
  }

  @PostMapping("/specialists/location")
  public ResponseEntity<StandardResponse> updateSpecialistLocation(@RequestBody SpecialistLocationReqDto input) {

    adminService.updateSpecialistLocation(input);
    return ResponseEntity.ok(new StandardResponse("location updated", "none"));
  }

  @GetMapping("/appointments")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<AppointmentsResDto>> getAppointments(@RequestParam(name = "specialist-wd", required = false) String specialistWD, @RequestParam(name = "patient-wd", required = false) String patientWD) {
    return ResponseEntity.ok(specialistService.getAppointments(specialistWD, patientWD));
  }
}
