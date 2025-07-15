package com.omnishore.cvtech.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score;

    @ManyToOne
    private CvFile cvFile;

    @ManyToOne
    private JobDescription jobDescription;
}