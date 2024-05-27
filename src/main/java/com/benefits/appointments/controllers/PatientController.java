package com.benefits.appointments.controllers;

import com.benefits.appointments.models.dto.input.AppointmentConfirmationInputDTO;
import com.benefits.appointments.models.dto.input.CreateAppointmentInputDTO;
import com.benefits.appointments.models.dto.output.CalendarEventsOutputDTO;
import com.benefits.appointments.models.dto.output.PatientAppointmentsDetailOutputDTO;
import com.benefits.appointments.models.dto.output.PatientOutputDTO;
import com.benefits.appointments.models.dto.output.StandardResponse;
import com.benefits.appointments.services.PatientService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/patients", produces = "application/json")
@PreAuthorize("hasRole('ROLE_PATIENT')")
@CrossOrigin(origins = "*")
public class PatientController {

  private final PatientService patientService;
  private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

  @Autowired
  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }

  @PostMapping("/create-appointment")
  public ResponseEntity<CalendarEventsOutputDTO> createAppointment(@Valid @RequestBody CreateAppointmentInputDTO appointment) {
    logger.info("Creating appointment");
    CalendarEventsOutputDTO response = patientService.createAppointment(appointment);
    return ResponseEntity.ok(response);
  }

  @GetMapping()
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST')")
  public ResponseEntity<List<PatientOutputDTO>> findPatientByWorkday(
      @RequestParam(name = "workday", required = false) String workday) {
    logger.info("Fetching patient by workday {}", workday);
    List<PatientOutputDTO> patients = patientService.getPatientsByWorkday(workday);
    return ResponseEntity.ok(patients);
  }

  @GetMapping("/{patientWD}/appointments/validations")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST','ROLE_PATIENT')")
  public ResponseEntity<PatientAppointmentsDetailOutputDTO> getAppointmentsValidation(@PathVariable String patientWD) {
    logger.info("fetching appointments detail for validation by workday= {}", patientWD);
    PatientAppointmentsDetailOutputDTO appointmentDetail = patientService.getAppointmentsValidation(patientWD);
    return ResponseEntity.ok(appointmentDetail);
  }

  @PatchMapping("/confirm-appointment")
  public ResponseEntity<StandardResponse> confirmAppointment(
      @Valid @RequestBody AppointmentConfirmationInputDTO appointmentConfirmationInputDTO) {
    logger.info("Creating appointment confirmation");
    patientService.confirmAppointment(appointmentConfirmationInputDTO);
    return ResponseEntity.ok(new StandardResponse("Confirmation received", "none"));
  }

}
