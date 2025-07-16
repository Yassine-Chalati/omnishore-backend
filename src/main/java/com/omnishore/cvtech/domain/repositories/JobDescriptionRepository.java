package com.omnishore.cvtech.domain.repositories;

import com.omnishore.cvtech.domain.entities.JobDescription;
import com.omnishore.cvtech.dtos.projections.JobDescriptionSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
    Page<JobDescriptionSummary> findAllBy(Pageable pageable);
}
