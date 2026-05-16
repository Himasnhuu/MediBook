package com.medibook.medicalrecord.repository;

import com.medibook.medicalrecord.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    // One record per appointment
    Optional<MedicalRecord> findByAppointmentId(
            Long appointmentId);

    // All records for a patient
    List<MedicalRecord> findByPatientId(
            Long patientId);

    // All records created by a provider
    List<MedicalRecord> findByProviderId(
            Long providerId);

    // Patient records sorted newest first
    List<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(
            Long patientId);

    // Records with a specific follow-up date
    List<MedicalRecord> findByFollowUpDate(
            LocalDate followUpDate);

    // Count records for a patient
    long countByPatientId(Long patientId);

    // Delete record by ID
    void deleteByRecordId(Long recordId);
}