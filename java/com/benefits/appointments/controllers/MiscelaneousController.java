package com.benefits.appointments.controllers;

import com.benefits.appointments.models.dto.output.AppointmentsResDto;
import com.benefits.appointments.models.dto.output.PatientConfirmationResDto;
import com.benefits.appointments.models.dto.output.SitesResponseDTO;
import com.benefits.appointments.services.MiscelaneousService;
import com.benefits.appointments.services.PatientService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class MiscelaneousController {

  @Autowired
  PatientService patientService;

  @Autowired
  MiscelaneousService miscelaneousService;

  @GetMapping("/appointments/confirmation-details")
  public ResponseEntity<PatientConfirmationResDto> findAppointmentById(@RequestParam(name = "id", required = true) String appointmentId) {
    return ResponseEntity.ok(patientService.findAppointmentById(appointmentId));
  }

  @GetMapping("/sites")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<SitesResponseDTO>> getAllSites() {
    return ResponseEntity.ok(miscelaneousService.getAllSites());
  }

  @GetMapping("/appointments")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST')")
  public ResponseEntity<AppointmentsResDto> getAppointment(@RequestParam(name = "id", required = true) Long appointmentId) {
    return ResponseEntity.ok(miscelaneousService.getAppointmentById(appointmentId));
  }
}
