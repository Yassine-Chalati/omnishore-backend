package com.omnishore.cvtech.domain.entities;

import com.omnishore.cvtech.commons.enums.JobDescriptionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate addedDate;

    @Enumerated(EnumType.STRING)
    private JobDescriptionType type;

    private String fileName;
    private String content;

    @OneToMany(mappedBy = "jobDescription", cascade = CascadeType.ALL)
    private List<Matching> matchings = new ArrayList<>();
}
