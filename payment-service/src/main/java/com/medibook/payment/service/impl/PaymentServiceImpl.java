package com.medibook.payment.service.impl;

import com.medibook.payment.entity.Payment;
import com.medibook.payment.entity.PaymentStatus;
import com.medibook.payment.event.PaymentEventPublisher;
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

	@Autowired
	private PaymentEventPublisher eventPublisher; // ← ADD

	@Override
	public Payment processPayment(Payment payment) {
		payment.setTransactionId(UUID.randomUUID().toString());
		payment.setStatus(PaymentStatus.PAID);
		payment.setPaidAt(LocalDateTime.now());
		if (payment.getCurrency() == null) {
			payment.setCurrency("INR");
		}
		Payment saved = paymentRepository.save(payment);

		// ← PUBLISH EVENT
		try {
			eventPublisher.publishSuccess(saved.getPatientId(), "", // email will be improved with Feign later
					String.valueOf(saved.getAmount()), saved.getTransactionId());
		} catch (Exception e) {
			System.err.println("Failed to publish payment success event: " + e.getMessage());
		}

		return saved;
	}

	@Override
	public Optional<Payment> getPaymentById(Long paymentId) {
		return paymentRepository.findById(paymentId);
	}

	@Override
	public Optional<Payment> getPaymentByAppointmentId(Long appointmentId) {
		return paymentRepository.findByAppointmentId(appointmentId);
	}

	@Override
	public List<Payment> getPaymentsByPatient(Long patientId) {
		return paymentRepository.findByPatientId(patientId);
	}

	@Override
	public List<Payment> getPaymentsByProvider(Long providerId) {
		return paymentRepository.findByProviderId(providerId);
	}

	@Override
	public List<Payment> getPaymentHistory() {
		return paymentRepository.findAll();
	}

	@Override
	public Payment refundPayment(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		if (payment.getStatus() != PaymentStatus.REFUND_REQUESTED) {
			throw new RuntimeException("Only REFUND_REQUESTED payments can be approved");
		}
		payment.setStatus(PaymentStatus.REFUNDED);
		payment.setRefundedAt(LocalDateTime.now());
		Payment saved = paymentRepository.save(payment);

		// ← PUBLISH EVENT
		try {
			eventPublisher.publishRefunded(saved.getPatientId(), "", // email will be improved with Feign later
					String.valueOf(saved.getAmount()), saved.getTransactionId());
		} catch (Exception e) {
			System.err.println("Failed to publish refund event: " + e.getMessage());
		}

		return saved;
	}

	@Override
	public Payment rejectRefund(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		if (payment.getStatus() != PaymentStatus.REFUND_REQUESTED) {
			throw new RuntimeException("Only REFUND_REQUESTED payments can be rejected");
		}
		payment.setStatus(PaymentStatus.REFUND_REJECTED);
		return paymentRepository.save(payment);
	}

	@Override
	public Payment requestRefund(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		if (payment.getStatus() != PaymentStatus.PAID) {
			throw new RuntimeException("Only PAID payments can have refund requested");
		}
		payment.setStatus(PaymentStatus.REFUND_REQUESTED);
		return paymentRepository.save(payment);
	}

	@Override
	public PaymentStatus getPaymentStatus(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		return payment.getStatus();
	}

	@Override
	public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		payment.setStatus(status);
		return paymentRepository.save(payment);
	}

	@Override
	public Double getTotalRevenue(Long providerId) {
		Double total = paymentRepository.getTotalRevenueByProvider(providerId);
		return total != null ? total : 0.0;
	}

	@Override
	public List<Payment> getPaymentsBetweenDates(LocalDateTime start, LocalDateTime end) {
		return paymentRepository.findByPaidAtBetween(start, end);
	}
}