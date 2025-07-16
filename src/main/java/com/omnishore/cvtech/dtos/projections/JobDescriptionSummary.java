package com.omnishore.cvtech.dtos.projections;

import com.omnishore.cvtech.commons.enums.JobDescriptionType;

import java.time.LocalDate;

public interface JobDescriptionSummary {
    Long getId();
    LocalDate getAddedDate();
    JobDescriptionType getType();
    String getFileName();
    String getContent();
}
