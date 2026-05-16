package com.medibook.review.service.impl;

import com.medibook.review.entity.Review;
import com.medibook.review.repository.ReviewRepository;
import com.medibook.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.medibook.review.event.ReviewEventPublisher;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;

	private final ReviewEventPublisher eventPublisher;	

	@Override
	public Review submitReview(Review review) {
		// One review per appointment
		if (reviewRepository.existsByAppointmentId(review.getAppointmentId())) {
			throw new RuntimeException("Review already submitted for appointmentId: " + review.getAppointmentId());
		}
		// Validate rating range
		if (review.getRating() < 1.0 || review.getRating() > 5.0) {
			throw new RuntimeException("Rating must be between 1.0 and 5.0");
		}
		review.setIsVisible(true);
		review.setCreatedAt(LocalDateTime.now());
		Review saved = reviewRepository.save(review); // ← assign to variable

		// Publish RabbitMQ event
		try {
			eventPublisher.publishReviewSubmitted(saved.getProviderId(), "", "A Patient", saved.getRating());
		} catch (Exception e) {
			System.err.println("Failed to publish review event: " + e.getMessage());
		}

		return saved; // ← return saved
	}

	@Override
	public Optional<Review> getReviewById(Long id) {
		return reviewRepository.findById(id);
	}

	@Override
	public Review getReviewByAppointment(Long appointmentId) {
		return reviewRepository.findByAppointmentId(appointmentId)
				.orElseThrow(() -> new RuntimeException("No review found for appointmentId: " + appointmentId));
	}

	@Override
	public List<Review> getReviewsByProvider(Long providerId) {
		return reviewRepository.findByProviderId(providerId);
	}

	@Override
	public List<Review> getVisibleReviewsByProvider(Long providerId) {
		return reviewRepository.findByProviderIdAndIsVisible(providerId, true);
	}

	@Override
	public List<Review> getReviewsByPatient(Long patientId) {
		return reviewRepository.findByPatientId(patientId);
	}

	@Override
	public Double getAverageRating(Long providerId) {
		Double avg = reviewRepository.calculateAverageRating(providerId);
		return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
	}

	@Override
	public Review hideReview(Long id) {
		Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
		review.setIsVisible(false);
		return reviewRepository.save(review);
	}

	@Override
	public Review showReview(Long id) {
		Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
		review.setIsVisible(true);
		return reviewRepository.save(review);
	}

	@Override
	public void deleteReview(Long id) {
		Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
		reviewRepository.delete(review);
	}

	@Override
	public boolean hasReview(Long appointmentId) {
		return reviewRepository.existsByAppointmentId(appointmentId);
	}

	@Override
	public List<Review> getAllReviews() {
		return reviewRepository.findAll();
	}
}