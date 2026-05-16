package com.medibook.appointment.event;

import com.medibook.appointment.security.RabbitMQConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishBooked(Long userId, String email,
            Long appointmentId, String doctorName, String date, String time) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId);
        event.put("recipientEmail", email);
        event.put("appointmentId", appointmentId);
        event.put("doctorName", doctorName);
        event.put("date", date);
        event.put("time", time);
        event.put("status", "BOOKED");
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.EXCHANGE,
            RabbitMQConstants.APPOINTMENT_BOOKED, event);
        System.out.println("Published: appointment.booked → " + email);
    }

    public void publishCancelled(Long userId, String email,
            Long appointmentId, String doctorName, String date) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId);
        event.put("recipientEmail", email);
        event.put("appointmentId", appointmentId);
        event.put("doctorName", doctorName);
        event.put("date", date);
        event.put("status", "CANCELLED");
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.EXCHANGE,
            RabbitMQConstants.APPOINTMENT_CANCELLED, event);
        System.out.println("Published: appointment.cancelled → " + email);
    }

    public void publishCompleted(Long userId, String email,
            Long appointmentId, String doctorName) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId);
        event.put("recipientEmail", email);
        event.put("appointmentId", appointmentId);
        event.put("doctorName", doctorName);
        event.put("status", "COMPLETED");
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.EXCHANGE,
            RabbitMQConstants.APPOINTMENT_COMPLETED, event);
        System.out.println("Published: appointment.completed → " + email);
    }
}