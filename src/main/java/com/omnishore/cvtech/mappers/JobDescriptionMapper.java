package com.omnishore.cvtech.mappers;

import com.omnishore.cvtech.annotations.Mapper;
import com.omnishore.cvtech.domain.entities.CvFile;
import com.omnishore.cvtech.domain.entities.JobDescription;
import com.omnishore.cvtech.domain.entities.Matching;
import com.omnishore.cvtech.dtos.jobdescription.CvFileDTO;
import com.omnishore.cvtech.dtos.jobdescription.JobDescriptionDTO;
import com.omnishore.cvtech.dtos.jobdescription.MatchingDTO;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public class JobDescriptionMapper {
    public JobDescriptionDTO toDto(JobDescription jobDescription) {
        if (jobDescription == null) return null;

        JobDescriptionDTO dto = new JobDescriptionDTO();
        dto.setId(jobDescription.getId());
        dto.setAddedDate(jobDescription.getAddedDate());
        dto.setType(jobDescription.getType());
        dto.setFileName(jobDescription.getFileName());
        dto.setContent(jobDescription.getContent());

        List<MatchingDTO> matchings = jobDescription.getMatchings().stream()
                .map(this::toMatchingDto)
                .collect(Collectors.toList());

        dto.setMatchings(matchings);
        return dto;
    }

    private MatchingDTO toMatchingDto(Matching matching) {
        MatchingDTO dto = new MatchingDTO();
        dto.setId(matching.getId());
        dto.setScore(matching.getScore());

        CvFile cv = matching.getCvFile();
        CvFileDTO cvDto = new CvFileDTO();
        cvDto.setId(cv.getId());
        cvDto.setFileName(cv.getFileName());
        cvDto.setAddedDate(cv.getAddedDate());
        cvDto.setFileType(cv.getFileType());
        cvDto.setImageUrl(cv.getImageUrl());

        dto.setCvFile(cvDto);
        return dto;
    }
}
