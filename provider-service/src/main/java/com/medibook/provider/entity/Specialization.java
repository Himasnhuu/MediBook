package com.medibook.provider.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "specializations")
@Data
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private Boolean isActive = true;
}