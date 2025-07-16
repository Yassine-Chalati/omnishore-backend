package com.omnishore.cvtech.domain.services.impl;

import com.omnishore.cvtech.client.ai.AiClient;
import com.omnishore.cvtech.commons.enums.JobDescriptionType;
import com.omnishore.cvtech.domain.entities.*;
import com.omnishore.cvtech.domain.repositories.CvFileRepository;
import com.omnishore.cvtech.domain.repositories.JobDescriptionRepository;
import com.omnishore.cvtech.domain.repositories.MatchingRepository;
import com.omnishore.cvtech.domain.services.JobDescriptionService;
import com.omnishore.cvtech.domain.services.MinioStorageService;
import com.omnishore.cvtech.dtos.projections.JobDescriptionSummary;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;

@Service
@AllArgsConstructor
public class JobDescriptionServiceImpl implements JobDescriptionService {
    private JobDescriptionRepository jobDescriptionRepository;
    private CvFileRepository cvFileRepository;
    private MatchingRepository matchingRepository;
    private MinioStorageService minioStorageService;
    private AiClient aiClient;

    @Override
    public JobDescription uploadJobDescriptionFile(MultipartFile file) throws Exception {

        // Save multipart file to temp file
        File originalTempFile = File.createTempFile("jobDescription_", "_" + file.getOriginalFilename());
        file.transferTo(originalTempFile);

        String contentType = file.getContentType();
        // If PDF or Word, convert to image (first page)
        if (!"application/pdf".equalsIgnoreCase(contentType)) {
            throw new RuntimeException("File not processed");
        }

        JobDescription jobDescription = new JobDescription();
        jobDescription.setType(JobDescriptionType.PDF);
        jobDescription.setAddedDate(LocalDate.now());
        String fileNameUrl = file.getOriginalFilename() + LocalDate.now().toString();

        minioStorageService.upload(originalTempFile, fileNameUrl);
        jobDescription.setContent(fileNameUrl);
        jobDescription.setFileName(fileNameUrl);


        cvFileRepository.findAll().forEach(cvFile -> {
            ResponseEntity<Map> response = aiClient.matchCvsWithJobDescriptionFile(originalTempFile, cvFile.getCvRawJson().getValue());

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = response.getBody();
                if (result == null) {
                    throw new RuntimeException("AI parsing returned empty body");
                }

                Matching matching = new Matching();
                matching.setScore((Double) result.get("matching_score"));
                matching.setJobDescription(jobDescription);
                matching.setCvFile(cvFile);

                jobDescription.getMatchings().add(matching);
                cvFile.getMatchings().add(matching);
                jobDescriptionRepository.save(jobDescription);
                cvFileRepository.save(cvFile);

                matchingRepository.save(matching);

            }
        });

        return jobDescription;
    }

    @Override
    public JobDescription uploadJobDescriptionPrompt(String prompt) throws Exception {

        // Save multipart file to temp file

        JobDescription jobDescription = new JobDescription();
        jobDescription.setType(JobDescriptionType.PROMPT);
        jobDescription.setAddedDate(LocalDate.now());

        String fileNameUrl = "prompt-" + LocalDate.now().toEpochDay();

        jobDescription.setFileName(fileNameUrl);
        jobDescription.setContent(prompt);



        cvFileRepository.findAll().forEach(cvFile -> {
            ResponseEntity<Map> response = aiClient.matchCvsWithJobDescriptionPrompt(prompt, cvFile.getCvRawJson().getValue());

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = response.getBody();
                if (result == null) {
                    throw new RuntimeException("AI parsing returned empty body");
                }

                Matching matching = new Matching();
                matching.setScore((Double) result.get("matching_score"));
                matching.setJobDescription(jobDescription);
                matching.setCvFile(cvFile);

                jobDescription.getMatchings().add(matching);
                cvFile.getMatchings().add(matching);
                jobDescriptionRepository.save(jobDescription);
                cvFileRepository.save(cvFile);

                matchingRepository.save(matching);

            }
        });

        return jobDescription;
    }

    @Override
    public Page<JobDescriptionSummary> getPaginatedJobDescription(Pageable pageable) {
        return jobDescriptionRepository.findAllBy(pageable);
    }

    @Override
    public JobDescription getJobDescriptionById(long id) {
        return jobDescriptionRepository.findById(id).get();
    }
}
