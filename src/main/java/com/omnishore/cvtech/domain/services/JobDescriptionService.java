package com.omnishore.cvtech.domain.services;

import com.omnishore.cvtech.domain.entities.JobDescription;
import com.omnishore.cvtech.dtos.projections.JobDescriptionSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface JobDescriptionService {
    JobDescription uploadJobDescriptionFile(MultipartFile file) throws Exception;

    JobDescription uploadJobDescriptionPrompt(String prompt) throws Exception;

    Page<JobDescriptionSummary> getPaginatedJobDescription(Pageable pageable);

    JobDescription getJobDescriptionById(long id);
}
