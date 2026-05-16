package com.medibook.review.service;

import com.medibook.review.entity.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review submitReview(Review review);

    Optional<Review> getReviewById(Long id);

    Review getReviewByAppointment(Long appointmentId);

    List<Review> getReviewsByProvider(Long providerId);

    List<Review> getVisibleReviewsByProvider(Long providerId);

    List<Review> getReviewsByPatient(Long patientId);

    Double getAverageRating(Long providerId);

    Review hideReview(Long id);

    Review showReview(Long id);

    void deleteReview(Long id);

    boolean hasReview(Long appointmentId);

    List<Review> getAllReviews();
}