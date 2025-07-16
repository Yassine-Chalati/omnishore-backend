package com.omnishore.cvtech.controllers;

import com.omnishore.cvtech.domain.entities.JobDescription;
import com.omnishore.cvtech.domain.services.JobDescriptionService;
import com.omnishore.cvtech.domain.services.MinioStorageService;
import com.omnishore.cvtech.dtos.projections.JobDescriptionSummary;
import com.omnishore.cvtech.mappers.JobDescriptionMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/job-description")
@AllArgsConstructor
@CrossOrigin
public class JobDescriptionController {
    private final JobDescriptionService jobDescriptionService;
    private final MinioStorageService minioStorageService;
    private final JobDescriptionMapper jobDescriptionMapper;

    @PostMapping("/upload/file")
    public ResponseEntity<?> uploadJobDescriptionFile(@RequestParam("file") MultipartFile file) {
        try {
            JobDescription jobDescription = jobDescriptionService.uploadJobDescriptionFile(file);
            return ResponseEntity.ok(jobDescriptionMapper.toDto(jobDescription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload one or more CVs: " + e.getMessage());
        }
    }

    @PostMapping("/upload/prompt")
    public ResponseEntity<?> uploadJobDescriptionPrompt(@RequestBody String prompt) {
        try {
            JobDescription jobDescription = jobDescriptionService.uploadJobDescriptionPrompt(prompt);
            return ResponseEntity.ok(jobDescriptionMapper.toDto(jobDescription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload one or more CVs: " + e.getMessage());
        }
    }


    @GetMapping("/download/{key}")
    public ResponseEntity<Resource> downloadJobDescriptionFile(@PathVariable String key) throws Exception {
        File temp = Files.createTempFile("download-", key).toFile();
        minioStorageService.download(key, temp);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(temp));
        String contentType = Files.probeContentType(temp.toPath());

        // Fallback to application/octet-stream if unknown
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .body(resource);
    }

    @GetMapping("/all")
    public Page<JobDescriptionSummary> getPaginatedJobDescription(Pageable pageable) {
        return jobDescriptionService.getPaginatedJobDescription(pageable);
    }

    @GetMapping("/matches/{jobDescriptionId}")
    public ResponseEntity<?> getJobDescriptionById(@PathVariable long jobDescriptionId) {
        try {
            JobDescription jobDescription = jobDescriptionService.getJobDescriptionById(jobDescriptionId);
            return ResponseEntity.ok(jobDescriptionMapper.toDto(jobDescription).getMatchings());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload one or more CVs: " + e.getMessage());
        }
    }
}
