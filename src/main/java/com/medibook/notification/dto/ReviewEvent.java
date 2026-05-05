package com.medibook.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReviewEvent {
    private Long providerId;
    private String providerEmail;
    private String patientName;
    private Double rating;
}