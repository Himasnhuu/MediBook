package com.medibook.medicalrecord.service;

import com.medibook.medicalrecord.entity.MedicalRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordService {

    // Create a new medical record
    MedicalRecord createRecord(MedicalRecord record);

    // Get record by ID
    Optional<MedicalRecord> getRecordById(Long recordId);

    // Get record by appointment ID
    Optional<MedicalRecord> getRecordByAppointmentId(
            Long appointmentId);

    // Get all records for a patient
    List<MedicalRecord> getRecordsByPatient(
            Long patientId);

    // Get all records by a provider
    List<MedicalRecord> getRecordsByProvider(
            Long providerId);

    // Get patient records newest first
    List<MedicalRecord> getRecordsByPatientSorted(
            Long patientId);

    // Update a record
    MedicalRecord updateRecord(
            Long recordId, MedicalRecord record);

    // Delete a record
    void deleteRecord(Long recordId);

    // Attach document URL
    MedicalRecord attachDocument(
            Long recordId, String attachmentUrl);

    // Get records with follow-up on a specific date
    List<MedicalRecord> getFollowUpRecords(
            LocalDate date);

    // Count records for a patient
    long getRecordCount(Long patientId);
}