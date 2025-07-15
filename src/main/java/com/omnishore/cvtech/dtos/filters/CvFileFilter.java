package com.omnishore.cvtech.dtos.filters;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CvFileFilter {
    private String fileName;
    private String fileType;
    private LocalDate addedDate;
}
