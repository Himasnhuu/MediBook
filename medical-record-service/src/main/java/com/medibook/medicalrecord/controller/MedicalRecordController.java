package com.medibook.medicalrecord.controller;

import com.medibook.medicalrecord.entity.MedicalRecord;
import com.medibook.medicalrecord.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService recordService;

    // Only PROVIDER can create a medical record
    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<MedicalRecord> createRecord(
            @RequestBody MedicalRecord record) {
        return ResponseEntity.ok(
            recordService.createRecord(record));
    }

    // PATIENT, PROVIDER or ADMIN can view by ID
    @GetMapping("/{recordId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<MedicalRecord> getById(
            @PathVariable Long recordId) {
        return ResponseEntity.ok(
            recordService.getRecordById(recordId)
                .orElseThrow(() -> new RuntimeException(
                    "Record not found")));
    }

    // PATIENT, PROVIDER or ADMIN can view by appointment
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<MedicalRecord> getByAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(
            recordService.getRecordByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException(
                    "Record not found")));
    }

    // PATIENT or ADMIN can view patient records
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<MedicalRecord>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
            recordService.getRecordsByPatient(patientId));
    }

    // PATIENT or ADMIN can view sorted records
    @GetMapping("/patient/{patientId}/sorted")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<MedicalRecord>> getByPatientSorted(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
            recordService.getRecordsByPatientSorted(patientId));
    }

    // PROVIDER or ADMIN can view provider records
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<List<MedicalRecord>> getByProvider(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(
            recordService.getRecordsByProvider(providerId));
    }

    // Only PROVIDER can update a record
    @PutMapping("/{recordId}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<MedicalRecord> updateRecord(
            @PathVariable Long recordId,
            @RequestBody MedicalRecord record) {
        return ResponseEntity.ok(
            recordService.updateRecord(recordId, record));
    }

    // Only PROVIDER can attach documents
    @PutMapping("/{recordId}/attach")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<MedicalRecord> attachDocument(
            @PathVariable Long recordId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
            recordService.attachDocument(
                recordId, body.get("attachmentUrl")));
    }

    // Public — used by notification-service
    @GetMapping("/followups")
    public ResponseEntity<List<MedicalRecord>> getFollowUps(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(
            recordService.getFollowUpRecords(date));
    }

    // PATIENT or ADMIN can get count
    @GetMapping("/count/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<Long> getCount(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
            recordService.getRecordCount(patientId));
    }

    // Only ADMIN can delete a record
    @DeleteMapping("/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRecord(
            @PathVariable Long recordId) {
        recordService.deleteRecord(recordId);
        return ResponseEntity.ok("Record deleted successfully");
    }
}