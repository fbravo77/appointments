package com.benefits.appointments.controllers;

import com.benefits.appointments.models.dto.input.AppointmentsReqDTO;
import com.benefits.appointments.models.dto.input.ConfirmAppointmentReqDTO;
import com.benefits.appointments.models.dto.output.CalendarEventsResDto;
import com.benefits.appointments.models.dto.output.PatientAppointmentsValidationResDTO;
import com.benefits.appointments.models.dto.output.PatientConfirmationResDto;
import com.benefits.appointments.models.dto.output.PatientsResponseDTO;
import com.benefits.appointments.models.dto.output.StandardResponse;
import com.benefits.appointments.services.PatientService;
import jakarta.validation.Valid;
import java.text.ParseException;
import java.util.List;
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
@CrossOrigin(origins = "*")
public class PatientController {

  @Autowired
  PatientService patientService;
  @PreAuthorize("hasRole('ROLE_PATIENT')")
  @PostMapping("/create-appointment")
  public ResponseEntity<CalendarEventsResDto> createAppointment(@Valid @RequestBody AppointmentsReqDTO appointment) {
    return ResponseEntity.ok(patientService.createAppointment(appointment));
  }
  @GetMapping()
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST')")
  public ResponseEntity<List<PatientsResponseDTO>> findByWorkday(@RequestParam(name = "workday", required = false) String workday) {
    return ResponseEntity.ok(patientService.getPatientsByWorkday(workday));
  }

  @GetMapping("/{patientWD}/appointments/validations")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST','ROLE_PATIENT')")
  public ResponseEntity<PatientAppointmentsValidationResDTO> getAppointmentsValidation(@PathVariable String patientWD) {
    return ResponseEntity.ok(patientService.getAppointmentsValidation(patientWD));
  }

  @PatchMapping("/confirm-appointment")
  public ResponseEntity<StandardResponse> confirmAppointment(@Valid @RequestBody ConfirmAppointmentReqDTO confirmAppointmentReqDTO) {
    patientService.confirmAppointment(confirmAppointmentReqDTO);
    return ResponseEntity.ok(new StandardResponse("Confirmation received", "none"));
  }

}
