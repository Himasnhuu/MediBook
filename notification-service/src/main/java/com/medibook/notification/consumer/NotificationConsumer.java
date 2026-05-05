package com.medibook.notification.consumer;

import com.medibook.notification.security.RabbitMQConstants;
import com.medibook.notification.dto.*;
import com.medibook.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_APPOINTMENT_BOOKED)
    public void handleAppointmentBooked(AppointmentEvent event) {
        try {
            System.out.println("RabbitMQ: appointment.booked → " + event.getRecipientEmail());
            notificationService.sendAppointmentBooked(
                event.getUserId(), event.getRecipientEmail(),
                event.getAppointmentId(), event.getDoctorName(),
                event.getDate(), event.getTime()
            );
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_APPOINTMENT_CANCELLED)
    public void handleAppointmentCancelled(AppointmentEvent event) {
        try {
            System.out.println("RabbitMQ: appointment.cancelled → " + event.getRecipientEmail());
            notificationService.sendAppointmentCancelled(
                event.getUserId(), event.getRecipientEmail(),
                event.getAppointmentId(), event.getDoctorName(),
                event.getDate()
            );
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_APPOINTMENT_COMPLETED)
    public void handleAppointmentCompleted(AppointmentEvent event) {
        try {
            System.out.println("RabbitMQ: appointment.completed → " + event.getRecipientEmail());
            notificationService.sendAppointmentCompleted(
                event.getUserId(), event.getRecipientEmail(),
                event.getAppointmentId(), event.getDoctorName()
            );
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_PAYMENT_SUCCESS)
    public void handlePaymentSuccess(PaymentEvent event) {
        try {
            System.out.println("RabbitMQ: payment.success → " + event.getRecipientEmail());
            notificationService.sendPaymentSuccess(
                event.getUserId(), event.getRecipientEmail(),
                event.getAmount(), event.getTransactionId()
            );
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_PAYMENT_REFUNDED)
    public void handlePaymentRefunded(PaymentEvent event) {
        try {
            System.out.println("RabbitMQ: payment.refunded → " + event.getRecipientEmail());
            notificationService.sendPaymentRefunded(
                event.getUserId(), event.getRecipientEmail(),
                event.getAmount(), event.getTransactionId()
            );
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_REVIEW_SUBMITTED)
    public void handleReviewSubmitted(ReviewEvent event) {
        try {
            System.out.println("RabbitMQ: review.submitted → provider " + event.getProviderId());
            notificationService.sendReviewReceived(
                event.getProviderId(), event.getProviderEmail(),
                event.getPatientName(), event.getRating()
            );
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}