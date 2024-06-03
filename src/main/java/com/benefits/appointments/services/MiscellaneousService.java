package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.output.AppointmentOutputDTO;
import com.benefits.appointments.models.dto.output.SiteOutputDTO;
import com.benefits.appointments.repositories.AppointmentRepository;
import com.benefits.appointments.repositories.SiteRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiscellaneousService {

  private final SiteRepository siteRepository;
  private final AppointmentRepository appointmentRepository;
  private static final Logger logger = LoggerFactory.getLogger(MiscellaneousService.class);

  public MiscellaneousService(SiteRepository siteRepository, AppointmentRepository appointmentRepository) {
    this.siteRepository = siteRepository;
    this.appointmentRepository = appointmentRepository;
  }

  @Transactional(readOnly = true)
  public List<SiteOutputDTO> getAllSites() {
    return siteRepository.findAll().stream().map(x -> new SiteOutputDTO(x.getId(), x.getName()))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public AppointmentOutputDTO getAppointmentById(Long id){
    return new AppointmentOutputDTO(appointmentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Appointment not found")));
  }
}
