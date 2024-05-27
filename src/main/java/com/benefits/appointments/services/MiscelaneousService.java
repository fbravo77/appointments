package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.output.AppointmentOutputDTO;
import com.benefits.appointments.models.dto.output.SiteOutputDTO;
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

  public List<SiteOutputDTO> getAllSites() {
    return siteRepository.findAll().stream().map(x -> new SiteOutputDTO(x.getId(), x.getName()))
        .collect(Collectors.toList());
  }

  public AppointmentOutputDTO getAppointmentById(Long id){
    return new AppointmentOutputDTO(appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found")));
  }
}
