package com.omnishore.cvtech.domain.repositories;

import com.omnishore.cvtech.domain.entities.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {
}
