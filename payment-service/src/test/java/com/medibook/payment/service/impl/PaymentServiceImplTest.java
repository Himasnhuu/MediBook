package com.medibook.payment.service.impl;

import com.medibook.payment.entity.Payment;
import com.medibook.payment.entity.PaymentMode;
import com.medibook.payment.entity.PaymentStatus;
import com.medibook.payment.event.PaymentEventPublisher;
import com.medibook.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setPaymentId(1L);
        payment.setAppointmentId(100L);
        payment.setPatientId(10L);
        payment.setProviderId(5L);
        payment.setAmount(500.0);
        payment.setStatus(PaymentStatus.PAID);
        payment.setMode(PaymentMode.UPI);
        payment.setTransactionId("TXN123");
        payment.setCurrency("INR");
        payment.setPaidAt(LocalDateTime.now());
    }

    // ── processPayment ──

    @Test
    void processPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.processPayment(payment);

        assertNotNull(result);
        assertEquals(PaymentStatus.PAID, result.getStatus());
        assertEquals("INR", result.getCurrency());
        assertNotNull(result.getPaidAt());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void processPayment_SetsCurrencyToINR_WhenNull() {
        payment.setCurrency(null);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.processPayment(payment);

        assertNotNull(result);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    // ── getPaymentById ──

    @Test
    void getPaymentById_Found() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.getPaymentById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getPaymentId());
    }

    @Test
    void getPaymentById_NotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.getPaymentById(99L);

        assertFalse(result.isPresent());
    }

    // ── getPaymentByAppointmentId ──

    @Test
    void getPaymentByAppointmentId_Found() {
        when(paymentRepository.findByAppointmentId(100L))
            .thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.getPaymentByAppointmentId(100L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getAppointmentId());
    }

    // ── refundPayment ──

    @Test
    void refundPayment_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.refundPayment(1L);

        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void refundPayment_NotPaid_ThrowsException() {
        payment.setStatus(PaymentStatus.REFUNDED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> paymentService.refundPayment(1L));

        assertEquals("Only PAID payments can be refunded", ex.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void refundPayment_NotFound_ThrowsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> paymentService.refundPayment(99L));
    }

    // ── getPaymentsByPatient ──

    @Test
    void getPaymentsByPatient_ReturnsList() {
        when(paymentRepository.findByPatientId(10L))
            .thenReturn(Arrays.asList(payment));

        List<Payment> results = paymentService.getPaymentsByPatient(10L);

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).getPatientId());
    }

    // ── getPaymentsByProvider ──

    @Test
    void getPaymentsByProvider_ReturnsList() {
        when(paymentRepository.findByProviderId(5L))
            .thenReturn(Arrays.asList(payment));

        List<Payment> results = paymentService.getPaymentsByProvider(5L);

        assertEquals(1, results.size());
    }

    // ── getPaymentStatus ──

    @Test
    void getPaymentStatus_ReturnsStatus() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentStatus status = paymentService.getPaymentStatus(1L);

        assertEquals(PaymentStatus.PAID, status);
    }

    @Test
    void getPaymentStatus_NotFound_ThrowsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> paymentService.getPaymentStatus(99L));
    }

    // ── getTotalRevenue ──

    @Test
    void getTotalRevenue_ReturnsTotal() {
        when(paymentRepository.getTotalRevenueByProvider(5L)).thenReturn(5000.0);

        Double total = paymentService.getTotalRevenue(5L);

        assertEquals(5000.0, total);
    }

    @Test
    void getTotalRevenue_ReturnsZero_WhenNull() {
        when(paymentRepository.getTotalRevenueByProvider(5L)).thenReturn(null);

        Double total = paymentService.getTotalRevenue(5L);

        assertEquals(0.0, total);
    }

    // ── updatePaymentStatus ──

    @Test
    void updatePaymentStatus_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.updatePaymentStatus(1L, PaymentStatus.REFUNDED);

        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
    }
}