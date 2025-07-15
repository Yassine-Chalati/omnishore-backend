package com.omnishore.cvtech.dtos.projections;

import java.time.LocalDate;

public interface CvFileSummary {
    Long getId();
    String getFileName();
    LocalDate getAddedDate();
    String getFileType();
    String getImageUrl();
}
