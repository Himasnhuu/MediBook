package com.medibook.payment.entity;

public enum PaymentStatus {
    PENDING,
    PAID,
    REFUNDED,
    REFUND_REQUESTED,
    REFUND_REJECTED,
    FAILED
}