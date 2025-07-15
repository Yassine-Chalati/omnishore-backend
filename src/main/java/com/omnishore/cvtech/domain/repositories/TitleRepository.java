package com.omnishore.cvtech.domain.repositories;

import com.omnishore.cvtech.domain.entities.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleRepository extends JpaRepository<Title, String> {
}
