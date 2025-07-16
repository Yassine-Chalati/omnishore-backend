package com.omnishore.cvtech.domain.services;

import com.omnishore.cvtech.domain.entities.CvFile;
import com.omnishore.cvtech.domain.entities.CvStructured;
import com.omnishore.cvtech.dtos.filters.CvFileFilter;
import com.omnishore.cvtech.dtos.projections.CvFileSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CvService {
    Page<CvFileSummary> getPaginatedCvFiles(Pageable pageable);

    CvStructured getCVStructuredByCvFileId(long id);

    Page<CvFileSummary> getFilteredPaginatedCvFiles(Pageable pageable, CvFileFilter filter);

    CvFile uploadCvFile(MultipartFile file) throws Exception;
}
