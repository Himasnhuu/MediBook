package com.medibook.payment.event;

import com.medibook.payment.security.RabbitMQConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishSuccess(Long userId, String email,
            String amount, String transactionId) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId);
        event.put("recipientEmail", email);
        event.put("amount", amount);
        event.put("transactionId", transactionId);
        event.put("status", "SUCCESS");
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.EXCHANGE,
            RabbitMQConstants.PAYMENT_SUCCESS, event);
        System.out.println("Published: payment.success → " + email);
    }

    public void publishRefunded(Long userId, String email,
            String amount, String transactionId) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId);
        event.put("recipientEmail", email);
        event.put("amount", amount);
        event.put("transactionId", transactionId);
        event.put("status", "REFUNDED");
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.EXCHANGE,
            RabbitMQConstants.PAYMENT_REFUNDED, event);
        System.out.println("Published: payment.refunded → " + email);
    }
}