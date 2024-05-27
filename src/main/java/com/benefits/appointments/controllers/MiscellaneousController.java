package com.benefits.appointments.controllers;

import com.benefits.appointments.models.dto.output.AppointmentOutputDTO;
import com.benefits.appointments.models.dto.output.AppointmentConfirmationOutputDTO;
import com.benefits.appointments.models.dto.output.SiteOutputDTO;
import com.benefits.appointments.services.MiscelaneousService;
import com.benefits.appointments.services.PatientService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class MiscellaneousController {

  private final PatientService patientService;
  private final MiscelaneousService miscelaneousService;
  private static final Logger logger = LoggerFactory.getLogger(MiscellaneousController.class);

  @Autowired
  public MiscellaneousController(PatientService patientService, MiscelaneousService miscelaneousService) {
    this.patientService = patientService;
    this.miscelaneousService = miscelaneousService;
  }

  @GetMapping("/appointments/confirmation-details")
  public ResponseEntity<AppointmentConfirmationOutputDTO> findAppointmentById(
      @RequestParam(name = "id", required = true) String appointmentId)
  {
    logger.info("Fetching appointment= {}", appointmentId);
    AppointmentConfirmationOutputDTO patientConfirmationResponse = patientService.findAppointmentById(appointmentId);
    return ResponseEntity.ok(patientConfirmationResponse);
  }

  @GetMapping("/sites")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<SiteOutputDTO>> getAllSites() {
    logger.info("Fetching all sites");
    List<SiteOutputDTO> sitesResponse = miscelaneousService.getAllSites();
    return ResponseEntity.ok(sitesResponse);
  }

  @GetMapping("/appointments")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST')")
  public ResponseEntity<AppointmentOutputDTO> getAppointment(
      @RequestParam(name = "id", required = true) Long appointmentId)
  {
    logger.info("Fetching appointment for confirmation = {}", appointmentId);
    AppointmentOutputDTO appointment = miscelaneousService.getAppointmentById(appointmentId);
    return ResponseEntity.ok(appointment);
  }
}
