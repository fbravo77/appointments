package com.benefits.appointments.security.repository;

import com.benefits.appointments.models.enums.RoleEnum;
import com.benefits.appointments.security.entity.Role;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends CrudRepository<Role, Integer> {
  Optional<Role> findByName(RoleEnum name);

}
