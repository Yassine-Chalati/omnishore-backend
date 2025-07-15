package com.omnishore.cvtech.domain.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnishore.cvtech.client.ai.AiClient;
import com.omnishore.cvtech.domain.entities.*;
import com.omnishore.cvtech.domain.repositories.CvFileRepository;
import com.omnishore.cvtech.domain.repositories.CvRawJsonRepository;
import com.omnishore.cvtech.domain.repositories.CvStructuredRepository;
import com.omnishore.cvtech.domain.services.CvService;
import com.omnishore.cvtech.domain.services.MinioStorageService;
import com.omnishore.cvtech.domain.specifications.CvFileSpecifications;
import com.omnishore.cvtech.dtos.filters.CvFileFilter;
import com.omnishore.cvtech.dtos.projections.CvFileSummary;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@Service
@AllArgsConstructor
public class CvServiceImpl implements CvService {
    private CvFileRepository cvFileRepository;
    private CvStructuredRepository cvStructuredRepository;
    private AiClient aiClient;
    private MinioStorageService minioStorageService;
    private CvRawJsonRepository cvRawJsonRepository;
    private ObjectMapper objectMapper;

    @Override
    public Page<CvFileSummary> getPaginatedCvFiles(Pageable pageable) {
        return cvFileRepository.findAllBy(pageable);
    }

    @Override
    public CvStructured getById(long id) {
        return cvFileRepository.findById(id).get().getCvStructured();
    }



    @Override
    public Page<CvFileSummary> getFilteredPaginatedCvFiles(Pageable pageable, CvFileFilter filter) {
        //return cvFileRepository.findAll(CvFileSpecifications.withFilters(filter), pageable, CvFileSummary.class);
        Page<CvFile> entityPage = cvFileRepository.findAll(CvFileSpecifications.withFilters(filter), pageable);

        // Map CvFile → CvFileSummary
        return entityPage.map(cv -> new CvFileSummary() {
            public Long getId() { return cv.getId(); }
            public String getFileName() { return cv.getFileName(); }
            public String getFileType() { return cv.getFileType(); }
            public String getImageUrl() { return cv.getImageUrl(); }
            public LocalDate getAddedDate() { return cv.getAddedDate(); }
        });
    }

//    @Override
//    public CvFile uploadCvFile(MultipartFile file) throws IOException {
//
//        // Save multipart file to temp file
//        File tempFile = File.createTempFile("cv_", "_" + file.getOriginalFilename());
//        file.transferTo(tempFile);
//
//
//        // Call AI client to parse CV content
//        ResponseEntity<Map> response = aiClient.parseCv(tempFile);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            Map<String, Object> result = response.getBody();
//            if (result == null) {
//                throw new RuntimeException("AI parsing returned empty body");
//            }
//
//            result.put("type_fichier", file.getContentType());
//
//            CvStructured structured = new CvStructured();
//            structured.setName((String) result.get("name"));
//            structured.setProfil((String) result.get("profil"));
//            structured.setTitleResume((String) result.get("resume_title"));
//
//            Contact contact = new Contact();
//            contact.setEmail((String) result.get("email"));
//            contact.setPhone((String) result.get("phone"));
//            contact.setAddress((String) result.get("address"));
//            contact.setLinkden((String) result.get("linkedin"));
//            contact.setGithub((String) result.get("github"));
//            structured.setContact(contact);
//
//            Title title = new Title();
//            title.setValue((String) result.get("title"));
//            structured.setTitle(title);
//
//            // Set collections using helper method
//            mapCollections(structured, result);
//
//            CvFile cvFile = new CvFile();
//            cvFile.setFileName(file.getOriginalFilename());
//            cvFile.setFileType(file.getContentType());
//            cvFile.setImageUrl((String) result.get("image_url"));
//            cvFile.setAddedDate(LocalDate.now());
//            cvFile.setCvStructured(structured);
//
//            // Save and return
//            return cvFileRepository.save(cvFile);
//        }
//
//        throw new RuntimeException("File not parsed");
//    }

    @Override
    public CvFile uploadCvFile(MultipartFile file) throws Exception {

        // Save multipart file to temp file
        File originalTempFile = File.createTempFile("cv_", "_" + file.getOriginalFilename());
        file.transferTo(originalTempFile);

        File imageForAI = originalTempFile; // default if already image
        String contentType = file.getContentType();
        System.out.println(contentType);
        // If PDF or Word, convert to image (first page)
        if ("application/pdf".equalsIgnoreCase(contentType)) {
            List<File> images = convertPdfToImages(originalTempFile);
            if (!images.isEmpty()) {
                imageForAI = images.get(0); // Use the first image
            }
        }

        // Call AI client to parse CV content using image
        ResponseEntity<Map> response = aiClient.parseCv(imageForAI);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> result = response.getBody();
            if (result == null) {
                throw new RuntimeException("AI parsing returned empty body");
            }

            result.put("type_fichier", contentType);

            CvStructured structured = new CvStructured();
            structured.setName((String) result.get("name"));
            structured.setProfil((String) result.get("profil"));
            structured.setTitleResume((String) result.get("resume_title"));

            Contact contact = new Contact();
            contact.setEmail((String) result.get("email"));
            contact.setPhone((String) result.get("phone"));
            contact.setAddress((String) result.get("address"));
            contact.setLinkden((String) result.get("linkedin"));
            contact.setGithub((String) result.get("github"));
            structured.setContact(contact);

            Title title = new Title();
            title.setValue((String) result.get("title"));
            structured.setTitle(title);

            // Set collections using helper method
            mapCollections(structured, result);

            String fileNameUrl = file.getOriginalFilename() + LocalDate.now().toString();

            minioStorageService.upload(originalTempFile, fileNameUrl);
            CvFile cvFile = new CvFile();
            cvFile.setFileName(file.getOriginalFilename());
            cvFile.setFileType(contentType);
            cvFile.setImageUrl(fileNameUrl);
            cvFile.setAddedDate(LocalDate.now());
            cvFile.setCvStructured(structured);
            CvRawJson cvRawJson = new CvRawJson(this.convertToJson(result));
            cvFile.setCvRawJson(cvRawJson);

            cvRawJsonRepository.save(cvRawJson);


            return cvFileRepository.save(cvFile);
        }

        throw new RuntimeException("File not parsed");
    }


    private void mapCollections(CvStructured structured, Map<String, Object> data) {
        structured.setSkills(parseCollection(data, "skills", Skill.class));
        structured.setExperiences(parseCollection(data, "experience", Experience.class));
        structured.setEducations(parseCollection(data, "education", Education.class));
        structured.setCertifications(parseCollection(data, "certifications", Certification.class));
        structured.setProjects(parseCollection(data, "projects", Project.class));
        structured.setLanguages(parseCollection(data, "languages", Language.class));
        structured.setInterests(parseCollection(data, "interests", Interest.class));
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> parseCollection(Map<String, Object> data, String key, Class<T> clazz) {
        Object value = data.get(key);
        if (value == null || !(value instanceof List<?> rawList)) {
            return Collections.emptyList();
        }

        return rawList.stream()
                .filter(item -> item instanceof String)
                .map(item -> {
                    try {
                        return clazz.getConstructor(String.class).newInstance((String) item);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create instance of " + clazz.getSimpleName(), e);
                    }
                })
                .map(clazz::cast)
                .toList();
    }

    private List<File> convertPdfToImages(File pdfFile) throws IOException {
        List<File> imageFiles = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 150);
                File imageFile = File.createTempFile("pdf_page_" + i + "_", ".png");
                ImageIO.write(image, "png", imageFile);
                imageFiles.add(imageFile);
            }
        }
        return imageFiles;
    }

    private String convertToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map); // compact version
            // return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map); // pretty version
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert map to JSON string", e);
        }
    }




//    private void mapCollections(CvStructured structured, Map<String, Object> data) {
//        structured.setSkills(parseCollection(data, "skills", Skill.class));
//        structured.setExperiences(parseCollection(data, "experience", Experience.class));
//        structured.setEducations(parseCollection(data, "education", Education.class));
//        structured.setCertifications(parseCollection(data, "certifications", Certification.class));
//        structured.setProjects(parseCollection(data, "projects", Project.class));
//        structured.setLanguages(parseCollection(data, "languages", Language.class));
//        structured.setInterests(parseCollection(data, "interests", Interest.class));
//    }
//
//
//    private <T> List<T> parseCollection(Map<String, Object> data, String key, Class<T> clazz) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Object value = data.get(key);
//        if (value == null) {
//            return Collections.emptyList();
//        }
//        return objectMapper.convertValue(value, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
//    }
}