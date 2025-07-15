package com.omnishore.cvtech.domain.repositories;

import com.omnishore.cvtech.domain.entities.CvStructured;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CvStructuredRepository extends JpaRepository<CvStructured, Long> {
}
