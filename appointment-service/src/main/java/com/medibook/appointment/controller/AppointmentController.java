package com.medibook.appointment.controller;

import com.medibook.appointment.entity.Appointment;
import com.medibook.appointment.entity.AppointmentStatus;
import com.medibook.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Only PATIENT can book an appointment
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment) {
        try {
            return ResponseEntity.ok(
                appointmentService.bookAppointment(appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // Public — anyone can view all appointments
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(
            appointmentService.getAllAppointments());
    }

    // Public — anyone can view single appointment
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getById(
            @PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Public — view patient appointments
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
            appointmentService.getByPatientId(patientId));
    }

    // Public — view provider appointments
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Appointment>> getByProvider(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(
            appointmentService.getByProviderId(providerId));
    }

    // Public — view appointments by date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Appointment>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(appointmentService.getByDate(date));
    }

    // Public — view provider appointments on date
    @GetMapping("/provider/{providerId}/date/{date}")
    public ResponseEntity<List<Appointment>> getByProviderAndDate(
            @PathVariable Long providerId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(
            appointmentService.getByProviderAndDate(providerId, date));
    }

    // Only ADMIN can manually update status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(
            appointmentService.updateStatus(id, status));
    }

    // PATIENT or ADMIN can cancel
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<Appointment> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(
            appointmentService.cancelAppointment(id));
    }

    // Only PROVIDER can complete an appointment
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Appointment> complete(@PathVariable Long id) {
        return ResponseEntity.ok(
            appointmentService.completeAppointment(id));
    }

    // Only ADMIN can delete an appointment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok("Appointment deleted");
    }

    // Public — get count by provider
    @GetMapping("/count/{providerId}")
    public ResponseEntity<Long> getCount(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(
            appointmentService.getCountByProviderId(providerId));
    }
}