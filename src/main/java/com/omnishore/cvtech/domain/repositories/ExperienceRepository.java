package com.omnishore.cvtech.domain.repositories;

import com.omnishore.cvtech.domain.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, String> {
}
