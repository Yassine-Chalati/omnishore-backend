package com.omnishore.cvtech.domain.services;

import com.omnishore.cvtech.domain.entities.JobDescription;
import org.springframework.web.multipart.MultipartFile;

public interface JobDescriptionService {
    JobDescription uploadJobDescription(MultipartFile file) throws Exception;
}
