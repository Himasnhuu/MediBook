package com.medibook.payment.controller;

import com.medibook.payment.entity.Payment;
import com.medibook.payment.entity.PaymentStatus;
import com.medibook.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Process a new payment
    @PostMapping
    public ResponseEntity<Payment> processPayment(
            @RequestBody Payment payment) {
        return ResponseEntity.ok(
                paymentService.processPayment(payment));
    }

    // Get payment by ID
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getById(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
                paymentService.getPaymentById(paymentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found")));
    }

    // Get payment by appointment ID
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Payment> getByAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(
                paymentService
                        .getPaymentByAppointmentId(
                                appointmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found")));
    }

    // Get all payments by patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Payment>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                paymentService
                        .getPaymentsByPatient(patientId));
    }

    // Get all payments by provider
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Payment>> getByProvider(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(
                paymentService
                        .getPaymentsByProvider(providerId));
    }

    // Get all payments (admin)
    @GetMapping
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(
                paymentService.getPaymentHistory());
    }

    // Refund a payment
    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<Payment> refund(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
                paymentService.refundPayment(paymentId));
    }

    // Get payment status
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<PaymentStatus> getStatus(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(
                paymentService
                        .getPaymentStatus(paymentId));
    }

    // Update payment status
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<Payment> updateStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(
                        paymentId, status));
    }

    // Get total revenue for provider
    @GetMapping("/revenue/{providerId}")
    public ResponseEntity<Double> getTotalRevenue(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(
                paymentService.getTotalRevenue(providerId));
    }

    // Get payments between dates
    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getHistory(
            @RequestParam @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime start,
            @RequestParam @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime end) {
        return ResponseEntity.ok(
                paymentService.getPaymentsBetweenDates(
                        start, end));
    }
}