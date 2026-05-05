package com.medibook.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PaymentEvent {
    private Long userId;
    private String recipientEmail;
    private String amount;
    private String transactionId;
    private String status;
}