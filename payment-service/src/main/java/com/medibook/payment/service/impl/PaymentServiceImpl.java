package com.medibook.payment.service.impl;

import com.medibook.payment.entity.Payment;
import com.medibook.payment.entity.PaymentStatus;
import com.medibook.payment.repository.PaymentRepository;
import com.medibook.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment processPayment(Payment payment) {
        // Generate unique transaction ID
        payment.setTransactionId(
                UUID.randomUUID().toString());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        // Default currency
        if (payment.getCurrency() == null) {
            payment.setCurrency("INR");
        }
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentById(
            Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public Optional<Payment> getPaymentByAppointmentId(
            Long appointmentId) {
        return paymentRepository
                .findByAppointmentId(appointmentId);
    }

    @Override
    public List<Payment> getPaymentsByPatient(
            Long patientId) {
        return paymentRepository
                .findByPatientId(patientId);
    }

    @Override
    public List<Payment> getPaymentsByProvider(
            Long providerId) {
        return paymentRepository
                .findByProviderId(providerId);
    }

    @Override
    public List<Payment> getPaymentHistory() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new RuntimeException(
                    "Only PAID payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public PaymentStatus getPaymentStatus(
            Long paymentId) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"));
        return payment.getStatus();
    }

    @Override
    public Payment updatePaymentStatus(
            Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    public Double getTotalRevenue(Long providerId) {
        Double total = paymentRepository
                .getTotalRevenueByProvider(providerId);
        return total != null ? total : 0.0;
    }

    @Override
    public List<Payment> getPaymentsBetweenDates(
            LocalDateTime start, LocalDateTime end) {
        return paymentRepository
                .findByPaidAtBetween(start, end);
    }
}