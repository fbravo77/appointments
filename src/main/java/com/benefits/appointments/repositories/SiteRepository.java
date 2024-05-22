package com.benefits.appointments.repositories;

import com.benefits.appointments.models.entities.Site;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

  Optional<Site> findByName(String name);
}
