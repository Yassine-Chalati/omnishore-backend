package com.omnishore.cvtech.domain.repositories;

import com.omnishore.cvtech.domain.entities.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<Interest, String> {
}
