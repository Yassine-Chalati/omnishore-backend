package com.omnishore.cvtech.domain.repositories;

import com.omnishore.cvtech.domain.entities.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, String> {
}
