package com.benefits.appointments.controllers;

import com.benefits.appointments.models.dto.input.AppUpdateReqDTO;
import com.benefits.appointments.models.dto.output.AppointmentsResDto;
import com.benefits.appointments.models.dto.output.CalendarEventsResDto;
import com.benefits.appointments.models.dto.output.PatientsResponseDTO;
import com.benefits.appointments.models.dto.output.ProfessionsOutDTO;
import com.benefits.appointments.models.dto.output.SpecialistOutDTO;
import com.benefits.appointments.models.dto.output.StandardResponse;
import com.benefits.appointments.services.SpecialistService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/specialists", produces = "application/json")
@PreAuthorize("hasRole('ROLE_SPECIALIST')")
@CrossOrigin(origins = "*")
public class SpecialistController {

  @Autowired
  SpecialistService specialistService;

  @GetMapping()
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PATIENT','ROLE_SPECIALIST')")
  public ResponseEntity<List<SpecialistOutDTO>> getSpecialists(@RequestParam(name = "profession", required = false) String profession) {
    return ResponseEntity.ok(specialistService.getSpecialists(profession));
  }

  @GetMapping("/professions")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PATIENT','ROLE_SPECIALIST')")
  public ResponseEntity<List<ProfessionsOutDTO>> getProfessions() {
    return ResponseEntity.ok(specialistService.getProfessions());
  }

  @GetMapping("/{specialistWD}/patients")
  public ResponseEntity<List<PatientsResponseDTO>> getPatients(@PathVariable String specialistWD) {
    return ResponseEntity.ok(specialistService.getPatients(specialistWD));
  }

  @GetMapping("/appointments")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST')")
  public ResponseEntity<List<AppointmentsResDto>> getAppointments(@RequestParam(name = "specialist-wd", required = true) String specialistWD, @RequestParam(name = "patient-wd", required = false) String patientWD) {
    return ResponseEntity.ok(specialistService.getAppointments(specialistWD, patientWD));
  }

  @GetMapping("/calendar")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST','ROLE_PATIENT')")
  public ResponseEntity<List<CalendarEventsResDto>> getCalendar(@RequestParam(name = "specialist-wd") String specialistWD)
      throws GeneralSecurityException, IOException {
    return ResponseEntity.ok(specialistService.getUnavailableDates(specialistWD));
  }

  @PostMapping("/update-appointment")
  public ResponseEntity<StandardResponse> updateAppointment(@Valid @RequestBody AppUpdateReqDTO appUpdateReqDTO) {
    specialistService.updateAppointment(appUpdateReqDTO);
    return ResponseEntity.ok(new StandardResponse("appointment updated", "none"));
  }

}
