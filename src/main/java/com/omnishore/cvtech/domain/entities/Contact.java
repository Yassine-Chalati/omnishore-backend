package com.omnishore.cvtech.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String email;
    @Lob
    private String phone;
    @Lob
    private String linkden;
    @Lob
    private String github;
    @Lob
    private String address;
}


