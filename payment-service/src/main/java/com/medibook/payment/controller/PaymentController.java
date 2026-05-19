package com.medibook.payment.controller;

import com.medibook.payment.entity.Payment;
import com.medibook.payment.entity.PaymentStatus;
import com.medibook.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Only PATIENT can process a payment
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Payment> processPayment(
            @RequestBody Payment payment) {
        return ResponseEntity.ok(
            paymentService.processPayment(payment));
    }

    // PATIENT, PROVIDER or ADMIN can view by ID
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Payment> getById(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
            paymentService.getPaymentById(paymentId)
                .orElseThrow(() -> new RuntimeException(
                    "Payment not found")));
    }

    // PATIENT, PROVIDER or ADMIN can view by appointment
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Payment> getByAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(
            paymentService.getPaymentByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException(
                    "Payment not found")));
    }

    // PATIENT can view own payments
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
            paymentService.getPaymentsByPatient(patientId));
    }

    // PROVIDER can view own earnings
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getByProvider(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(
            paymentService.getPaymentsByProvider(providerId));
    }

    // Only ADMIN can view all payments
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(
            paymentService.getPaymentHistory());
    }

    // PATIENT can request refund (pending admin approval)
    @PutMapping("/{paymentId}/request-refund")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Payment> requestRefund(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
            paymentService.requestRefund(paymentId));
    }

    // Only ADMIN can process actual refund
    @PutMapping("/{paymentId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payment> refund(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
            paymentService.refundPayment(paymentId));
    }

    // Public — anyone can check payment status
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<PaymentStatus> getStatus(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
            paymentService.getPaymentStatus(paymentId));
    }

    // Only ADMIN can manually update payment status
    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payment> updateStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(
            paymentService.updatePaymentStatus(paymentId, status));
    }

    // PROVIDER or ADMIN can view revenue
    @GetMapping("/revenue/{providerId}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Double> getTotalRevenue(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(
            paymentService.getTotalRevenue(providerId));
    }

    // Only ADMIN can view payment history between dates
    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getHistory(
            @RequestParam @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime start,
            @RequestParam @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime end) {
        return ResponseEntity.ok(
            paymentService.getPaymentsBetweenDates(start, end));
    }
}