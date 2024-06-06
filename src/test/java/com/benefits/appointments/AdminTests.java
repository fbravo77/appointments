package com.benefits.appointments;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.benefits.appointments.models.dto.output.PatientOutputDTO;
import com.benefits.appointments.models.entities.Account;
import com.benefits.appointments.models.entities.Patient;
import com.benefits.appointments.models.enums.RoleEnum;
import com.benefits.appointments.security.entity.Role;
import com.benefits.appointments.security.entity.User;
import com.benefits.appointments.security.repository.RolRepository;
import com.benefits.appointments.security.repository.UserRepository;
import com.benefits.appointments.services.AdminService;
import com.github.dozermapper.core.Mapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AdminTests {
  @InjectMocks
  private AdminService adminService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Mapper mapper;

  @Mock
  private RolRepository roleRepository;

  @BeforeEach
  public void initializeVariables(){
    when(roleRepository.findById(RoleEnum.ROLE_PATIENT.getId())).thenReturn(Optional.of(new Role(RoleEnum.ROLE_PATIENT)));
  }

  @Test
  public void testGetAllPatients_ShouldReturnEmptyList() throws Exception {
    // Arrange
    when(userRepository.findByRole(any(Role.class))).thenReturn(Collections.emptyList());

    // Call the method
    List<PatientOutputDTO> patients = adminService.getAllPatients();

    // Verify results
    assertThat(patients).isNotNull();
    assertThat(patients.isEmpty());
  }

  @Test
  public void testGetAllPatients_withPatients() throws Exception {
    // Mock dependencies
    Role role = new Role(RoleEnum.ROLE_PATIENT);
    User user1 = new User();
    User user2 = new User();
    Patient patient1 = new Patient();
    patient1.setAccount(new Account("location","location"));
    user1.setPatient(patient1);
    user2.setPatient(patient1);
    List<User> userList = Arrays.asList(user1, user2);
    PatientOutputDTO patientDto1 = new PatientOutputDTO();
    PatientOutputDTO patientDto2 = new PatientOutputDTO();
    when(roleRepository.findById(RoleEnum.ROLE_PATIENT.getId())).thenReturn(Optional.of(role));
    when(userRepository.findByRole(role)).thenReturn(userList);

    // Call the method
    List<PatientOutputDTO> patients = adminService.getAllPatients();

    // Verify results
    assertThat(patients).isNotNull();
    assertThat(patients.size()).isEqualTo(2);
  //  assertThat(patients). containsExactlyInAnyOrder(patientDto1, patientDto2);
  }
}
