package com.medibook.review.repository;

import com.medibook.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByAppointmentId(Long appointmentId);

    List<Review> findByProviderId(Long providerId);

    List<Review> findByPatientId(Long patientId);

    List<Review> findByProviderIdAndIsVisible(Long providerId, Boolean isVisible);

    boolean existsByAppointmentId(Long appointmentId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.providerId = :providerId AND r.isVisible = true")
    Double calculateAverageRating(@Param("providerId") Long providerId);

    long countByProviderId(Long providerId);
}