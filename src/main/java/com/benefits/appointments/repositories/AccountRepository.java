package com.benefits.appointments.repositories;

import com.benefits.appointments.models.entities.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  Optional<Account> findByName(String name);

}
