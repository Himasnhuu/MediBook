package com.medibook.review.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = @UniqueConstraint(columnNames = "appointment_id")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    private Long patientId;
    private Long providerId;

    private Double rating;
    private String comment;

    private Boolean isVisible = true;

    private LocalDateTime createdAt;
}