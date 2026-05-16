package com.medibook.provider.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;
    
 // Add these fields to store pending changes for provider profile updates
    private String pendingSpecialization;
    private String pendingQualification;
    private Integer pendingExperienceYears;
    private String pendingBio;
    private String pendingClinicName;
    private String pendingClinicAddress;
    private Double pendingConsultationFee;
    private Boolean hasPendingChanges; // default false
    
    
    private Double consultationFee; // default 0.0
    private Long userId;
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private String bio;
    private String clinicName;
    private String clinicAddress;
    private Double avgRating = 0.0;
    private Boolean isVerified = false;
    private Boolean isAvailable = true;
    private LocalDate createdAt;
    
    private String doctorName;
    
    @Column(columnDefinition = "LONGTEXT")
    private String profilePhotoUrl;
}