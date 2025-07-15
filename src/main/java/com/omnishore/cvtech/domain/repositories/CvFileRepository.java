package com.omnishore.cvtech.domain.repositories;

import com.omnishore.cvtech.domain.entities.CvFile;
import com.omnishore.cvtech.dtos.projections.CvFileSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CvFileRepository extends JpaRepository<CvFile, Long>, JpaSpecificationExecutor<CvFile> {
    Page<CvFileSummary> findAllBy(Pageable pageable);
    //<T> Page<T> findAll(Specification<CvFile> spec, Pageable pageable, Class<T> type);

}
