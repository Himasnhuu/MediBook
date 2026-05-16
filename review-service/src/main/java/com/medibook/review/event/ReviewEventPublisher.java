package com.medibook.review.event;

import com.medibook.review.security.RabbitMQConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReviewEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishReviewSubmitted(Long providerId,
            String providerEmail, String patientName, Double rating) {
        Map<String, Object> event = new HashMap<>();
        event.put("providerId", providerId);
        event.put("providerEmail", providerEmail);
        event.put("patientName", patientName);
        event.put("rating", rating);
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.EXCHANGE,
            RabbitMQConstants.REVIEW_SUBMITTED, event);
        System.out.println("Published: review.submitted → provider " + providerId);
    }
}