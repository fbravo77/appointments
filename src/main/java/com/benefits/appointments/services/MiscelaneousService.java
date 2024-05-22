package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.output.AppointmentsResDto;
import com.benefits.appointments.models.dto.output.SitesResponseDTO;
import com.benefits.appointments.models.entities.Site;
import com.benefits.appointments.repositories.AppointmentRepository;
import com.benefits.appointments.repositories.SiteRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiscelaneousService {

  @Autowired
  SiteRepository siteRepository;
  @Autowired
  AppointmentRepository appointmentRepository;

  public List<SitesResponseDTO> getAllSites() {
    return siteRepository.findAll().stream().map(x -> new SitesResponseDTO(x.getId(), x.getName()))
        .collect(Collectors.toList());
  }

  public AppointmentsResDto getAppointmentById(Long id){
    return new AppointmentsResDto(appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found")));
  }
}
