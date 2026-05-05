package com.medibook.review.controller;

import com.medibook.review.entity.Review;
import com.medibook.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	// Only PATIENT can submit a review
	@PostMapping
	@PreAuthorize("hasRole('PATIENT')")
	public ResponseEntity<?> submitReview(@RequestBody Review review) {
		try {
			return ResponseEntity.ok(reviewService.submitReview(review));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	// Public — anyone can view all reviews
	@GetMapping
	public ResponseEntity<List<Review>> getAllReviews() {
		return ResponseEntity.ok(reviewService.getAllReviews());
	}

	// Public — anyone can view single review
	@GetMapping("/{id}")
	public ResponseEntity<Review> getById(@PathVariable Long id) {
		return reviewService.getReviewById(id).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Public — anyone can view review by appointment
	@GetMapping("/appointment/{appointmentId}")
	public ResponseEntity<?> getByAppointment(@PathVariable Long appointmentId) {
		try {
			return ResponseEntity.ok(reviewService.getReviewByAppointment(appointmentId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// Public — anyone can view provider reviews
	@GetMapping("/provider/{providerId}")
	public ResponseEntity<List<Review>> getByProvider(@PathVariable Long providerId) {
		return ResponseEntity.ok(reviewService.getReviewsByProvider(providerId));
	}

	// Public — anyone can view visible reviews
	@GetMapping("/provider/{providerId}/visible")
	public ResponseEntity<List<Review>> getVisibleByProvider(@PathVariable Long providerId) {
		return ResponseEntity.ok(reviewService.getVisibleReviewsByProvider(providerId));
	}

	// Public — anyone can view average rating
	@GetMapping("/provider/{providerId}/rating")
	public ResponseEntity<Double> getAverageRating(@PathVariable Long providerId) {
		return ResponseEntity.ok(reviewService.getAverageRating(providerId));
	}

	// PATIENT or ADMIN can view patient reviews
	@GetMapping("/patient/{patientId}")
	@PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
	public ResponseEntity<List<Review>> getByPatient(@PathVariable Long patientId) {
		return ResponseEntity.ok(reviewService.getReviewsByPatient(patientId));
	}

	// Public — check if appointment has review
	@GetMapping("/appointment/{appointmentId}/exists")
	public ResponseEntity<Boolean> hasReview(@PathVariable Long appointmentId) {
		return ResponseEntity.ok(reviewService.hasReview(appointmentId));
	}

	// Only ADMIN can hide a review
	@PutMapping("/{id}/hide")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Review> hideReview(@PathVariable Long id) {
		return ResponseEntity.ok(reviewService.hideReview(id));
	}

	// Only ADMIN can show a review
	@PutMapping("/{id}/show")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Review> showReview(@PathVariable Long id) {
		return ResponseEntity.ok(reviewService.showReview(id));
	}

	// Only ADMIN can delete a review
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteReview(@PathVariable Long id) {
		reviewService.deleteReview(id);
		return ResponseEntity.ok("Review deleted");
	}
}