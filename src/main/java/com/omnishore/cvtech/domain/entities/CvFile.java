package com.omnishore.cvtech.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class CvFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String fileName;
    @Lob
    private LocalDate addedDate;
    @Lob
    private String fileType;
    @Lob
    private String imageUrl;

    @OneToOne(cascade = CascadeType.ALL)
    private CvStructured cvStructured;

    @OneToOne(cascade = CascadeType.ALL)
    private CvRawJson  cvRawJson;

    @OneToMany(mappedBy = "cvFile", cascade = CascadeType.ALL)
    private List<Matching> matchings = new ArrayList<>();
}
