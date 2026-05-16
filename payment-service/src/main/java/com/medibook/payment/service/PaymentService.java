package com.medibook.payment.service;

import com.medibook.payment.entity.Payment;
import com.medibook.payment.entity.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    // Process a new payment
    Payment processPayment(Payment payment);

    // Get payment by ID
    Optional<Payment> getPaymentById(Long paymentId);

    // Get payment by appointment ID
    Optional<Payment> getPaymentByAppointmentId(
            Long appointmentId);

    // Get all payments by patient
    List<Payment> getPaymentsByPatient(Long patientId);

    // Get all payments by provider
    List<Payment> getPaymentsByProvider(Long providerId);

    // Get payment history (all payments)
    List<Payment> getPaymentHistory();

    // Process a refund
    Payment refundPayment(Long paymentId);

    // Get payment status
    PaymentStatus getPaymentStatus(Long paymentId);

    // Update payment status
    Payment updatePaymentStatus(
            Long paymentId, PaymentStatus status);

    // Get total revenue for a provider
    Double getTotalRevenue(Long providerId);

    // Get payments between dates
    List<Payment> getPaymentsBetweenDates(
            LocalDateTime start, LocalDateTime end);
}