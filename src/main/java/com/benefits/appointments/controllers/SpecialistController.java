package com.benefits.appointments.controllers;

import com.benefits.appointments.models.dto.input.UpdateAppointmentInputDTO;
import com.benefits.appointments.models.dto.output.AppointmentOutputDTO;
import com.benefits.appointments.models.dto.output.CalendarEventsOutputDTO;
import com.benefits.appointments.models.dto.output.PatientOutputDTO;
import com.benefits.appointments.models.dto.output.ProfessionOutputDTO;
import com.benefits.appointments.models.dto.output.SpecialistOutputDTO;
import com.benefits.appointments.models.dto.output.StandardResponse;
import com.benefits.appointments.services.SpecialistService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/specialists", produces = "application/json")
@PreAuthorize("hasRole('ROLE_SPECIALIST')")
@CrossOrigin(origins = "*")
public class SpecialistController {

  private final SpecialistService specialistService;
  private static final Logger logger = LoggerFactory.getLogger(SpecialistController.class);

  public SpecialistController(SpecialistService specialistService) {
    this.specialistService = specialistService;
  }

  @GetMapping()
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PATIENT','ROLE_SPECIALIST')")
  public ResponseEntity<List<SpecialistOutputDTO>> getSpecialists(
      @RequestParam(name = "profession", required = false) String profession) {
    logger.info("fetching all the specialists");
    List<SpecialistOutputDTO> specialists = specialistService.getSpecialists(profession);
    return ResponseEntity.ok(specialists);
  }

  @GetMapping("/professions")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PATIENT','ROLE_SPECIALIST')")
  public ResponseEntity<List<ProfessionOutputDTO>> getProfessions() {
    logger.info("fetching all the professions");
    List<ProfessionOutputDTO> professions = specialistService.getProfessions();
    return ResponseEntity.ok(professions);
  }

  @GetMapping("/{specialistWD}/patients")
  public ResponseEntity<List<PatientOutputDTO>> getPatients(@PathVariable String specialistWD) {
    logger.info("get patients by specialist workday= {}", specialistWD);
    List<PatientOutputDTO> patients = specialistService.getPatients(specialistWD);
    return ResponseEntity.ok(patients);
  }

  @GetMapping("/appointments")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST')")
  public ResponseEntity<List<AppointmentOutputDTO>> getAppointments(
      @RequestParam(name = "specialist-wd", required = true) String specialistWD,
      @RequestParam(name = "patient-wd", required = false) String patientWD)
  {
    logger.info("get appointments by specialist wd = {} and patient wd = {}", specialistWD, patientWD);
    return ResponseEntity.ok(specialistService.getAppointments(specialistWD, patientWD));
  }

  @GetMapping("/calendar")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SPECIALIST','ROLE_PATIENT')")
  public ResponseEntity<List<CalendarEventsOutputDTO>> getCalendar(
      @RequestParam(name = "specialist-wd") String specialistWD)
      throws GeneralSecurityException, IOException
  {
    logger.info("fetching calendar for specialist workday = {}", specialistWD);
    List<CalendarEventsOutputDTO> calendar = specialistService.getUnavailableDates(specialistWD);
    return ResponseEntity.ok(calendar);
  }

  @PatchMapping("/update-appointment")
  public ResponseEntity<StandardResponse> updateAppointment(@Valid @RequestBody UpdateAppointmentInputDTO updateAppointmentInputDTO) {
    logger.info("updating appointment");
    specialistService.updateAppointment(updateAppointmentInputDTO);
    return ResponseEntity.ok(new StandardResponse("appointment updated", "none"));
  }
}
