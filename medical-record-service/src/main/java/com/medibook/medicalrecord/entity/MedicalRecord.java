package com.medibook.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(unique = true)
    private Long appointmentId;

    private Long patientId;

    private Long providerId;

    private String diagnosis;

    @Column(length = 2000)
    private String prescription;

    @Column(length = 2000)
    private String notes;

    private String attachmentUrl;

    private LocalDate followUpDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}