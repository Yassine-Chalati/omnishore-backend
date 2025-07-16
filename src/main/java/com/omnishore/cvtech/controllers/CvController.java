package com.omnishore.cvtech.controllers;

import com.omnishore.cvtech.domain.entities.CvFile;
import com.omnishore.cvtech.domain.entities.CvStructured;
import com.omnishore.cvtech.domain.services.CvService;
import com.omnishore.cvtech.domain.services.MinioStorageService;
import com.omnishore.cvtech.dtos.projections.CvFileSummary;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cv")
@AllArgsConstructor
@CrossOrigin
public class CvController {
    private final CvService cvService;
    private final MinioStorageService minioStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMultipleCvs(@RequestParam("file") MultipartFile file) {
        try {
                CvFile savedCv = cvService.uploadCvFile(file);
            return ResponseEntity.ok(savedCv);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload one or more CVs: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public Page<CvFileSummary> getPaginatedCvFiles(Pageable pageable) {
        return cvService.getPaginatedCvFiles(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCVStructuredByCvFileId(@PathVariable Long id) {
        return ResponseEntity.ok(cvService.getCVStructuredByCvFileId(id));
    }


    @GetMapping("/download/{key}")
    public ResponseEntity<Resource> download(@PathVariable String key) throws Exception {
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

}
