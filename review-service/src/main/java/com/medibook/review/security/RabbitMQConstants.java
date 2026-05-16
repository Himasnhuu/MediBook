package com.medibook.review.security;

public class RabbitMQConstants {
    public static final String EXCHANGE                    = "medibook.exchange";
    public static final String APPOINTMENT_BOOKED          = "appointment.booked";
    public static final String APPOINTMENT_CANCELLED       = "appointment.cancelled";
    public static final String APPOINTMENT_COMPLETED       = "appointment.completed";
    public static final String PAYMENT_SUCCESS             = "payment.success";
    public static final String PAYMENT_REFUNDED            = "payment.refunded";
    public static final String REVIEW_SUBMITTED            = "review.submitted";
    public static final String QUEUE_APPOINTMENT_BOOKED    = "queue.appointment.booked";
    public static final String QUEUE_APPOINTMENT_CANCELLED = "queue.appointment.cancelled";
    public static final String QUEUE_APPOINTMENT_COMPLETED = "queue.appointment.completed";
    public static final String QUEUE_PAYMENT_SUCCESS       = "queue.payment.success";
    public static final String QUEUE_PAYMENT_REFUNDED      = "queue.payment.refunded";
    public static final String QUEUE_REVIEW_SUBMITTED      = "queue.review.submitted";
}