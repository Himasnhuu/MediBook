package com.medibook.payment.repository;

import com.medibook.payment.entity.Payment;
import com.medibook.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    // Find payment by appointment
    Optional<Payment> findByAppointmentId(Long appointmentId);

    // Find all payments by patient
    List<Payment> findByPatientId(Long patientId);

    // Find all payments by provider
    List<Payment> findByProviderId(Long providerId);

    // Find by status
    List<Payment> findByStatus(PaymentStatus status);

    // Find by transaction ID
    Optional<Payment> findByTransactionId(String transactionId);

    // Sum total amount paid by a patient
    @Query("SELECT SUM(p.amount) FROM Payment p " +
           "WHERE p.patientId = :patientId " +
           "AND p.status = 'PAID'")
    Double sumAmountByPatientId(
            @Param("patientId") Long patientId);

    // Find payments between dates
    List<Payment> findByPaidAtBetween(
            LocalDateTime start, LocalDateTime end);

    // Total revenue for a provider
    @Query("SELECT SUM(p.amount) FROM Payment p " +
           "WHERE p.providerId = :providerId " +
           "AND p.status = 'PAID'")
    Double getTotalRevenueByProvider(
            @Param("providerId") Long providerId);
}