package com.omnishore.cvtech.dtos.jobdescription;

import com.omnishore.cvtech.commons.enums.JobDescriptionType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JobDescriptionDTO {
    private Long id;
    private LocalDate addedDate;
    private JobDescriptionType type;
    private String fileName;
    private String content;
    private List<MatchingDTO> matchings;
}
