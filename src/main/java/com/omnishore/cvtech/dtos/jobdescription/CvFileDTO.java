package com.omnishore.cvtech.dtos.jobdescription;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CvFileDTO {
    private Long id;
    private String fileName;
    private LocalDate addedDate;
    private String fileType;
    private String imageUrl;
}
