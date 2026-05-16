package com.medibook.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AppointmentEvent {
    private Long userId;
    private String recipientEmail;
    private Long appointmentId;
    private String doctorName;
    private String date;
    private String time;
    private String status;
}