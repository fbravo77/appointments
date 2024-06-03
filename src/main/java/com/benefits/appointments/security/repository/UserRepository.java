package com.benefits.appointments.security.repository;

import com.benefits.appointments.security.entity.Role;
import com.benefits.appointments.security.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
  Optional<User> findByWorkday(String workday);
  Optional<User> findByWorkdayAndRole(String workday,Role role);

  List<User> findByRole(Role role);
  List<User> findByWorkdayContainingIgnoreCaseAndRole(String workday,Role role);
  List<User> findByWorkdayNotInAndRoleAndIsActive(List<String> workdays,Role role,boolean active);

  @Query("SELECT u.workday FROM User u WHERE u.workday IN ?1 AND u.role = ?2 AND u.isActive  = ?3")
  List<String> findByWorkdayInAndRoleAndIsActive(Set<String> workdays, Role role, boolean active);

}
