package com.omnishore.cvtech.dtos.jobdescription;

import lombok.Data;

@Data
public class MatchingDTO {
    private Long id;
    private double score;
    private CvFileDTO cvFile;
}
