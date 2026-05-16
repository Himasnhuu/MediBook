package com.medibook.medicalrecord.service.impl;

import com.medibook.medicalrecord.entity.MedicalRecord;
import com.medibook.medicalrecord.repository
        .MedicalRecordRepository;
import com.medibook.medicalrecord.service
        .MedicalRecordService;
import org.springframework.beans.factory.annotation
        .Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordServiceImpl
        implements MedicalRecordService {

    @Autowired
    private MedicalRecordRepository recordRepository;

    @Override
    public MedicalRecord createRecord(
            MedicalRecord record) {
        // Check if record already exists
        // for this appointment
        if (recordRepository.findByAppointmentId(
                record.getAppointmentId()).isPresent()) {
            throw new RuntimeException(
                "Medical record already exists " +
                "for this appointment");
        }
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Override
    public Optional<MedicalRecord> getRecordById(
            Long recordId) {
        return recordRepository.findById(recordId);
    }

    @Override
    public Optional<MedicalRecord> getRecordByAppointmentId(
            Long appointmentId) {
        return recordRepository
                .findByAppointmentId(appointmentId);
    }

    @Override
    public List<MedicalRecord> getRecordsByPatient(
            Long patientId) {
        return recordRepository
                .findByPatientId(patientId);
    }

    @Override
    public List<MedicalRecord> getRecordsByProvider(
            Long providerId) {
        return recordRepository
                .findByProviderId(providerId);
    }

    @Override
    public List<MedicalRecord> getRecordsByPatientSorted(
            Long patientId) {
        return recordRepository
                .findByPatientIdOrderByCreatedAtDesc(
                        patientId);
    }

    @Override
    public MedicalRecord updateRecord(
            Long recordId, MedicalRecord updatedRecord) {
        MedicalRecord record = recordRepository
                .findById(recordId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Record not found"));
        record.setDiagnosis(
                updatedRecord.getDiagnosis());
        record.setPrescription(
                updatedRecord.getPrescription());
        record.setNotes(updatedRecord.getNotes());
        record.setFollowUpDate(
                updatedRecord.getFollowUpDate());
        record.setUpdatedAt(LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Override
    public void deleteRecord(Long recordId) {
        recordRepository.deleteById(recordId);
    }

    @Override
    public MedicalRecord attachDocument(
            Long recordId, String attachmentUrl) {
        MedicalRecord record = recordRepository
                .findById(recordId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Record not found"));
        record.setAttachmentUrl(attachmentUrl);
        record.setUpdatedAt(LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Override
    public List<MedicalRecord> getFollowUpRecords(
            LocalDate date) {
        return recordRepository
                .findByFollowUpDate(date);
    }

    @Override
    public long getRecordCount(Long patientId) {
        return recordRepository
                .countByPatientId(patientId);
    }
}