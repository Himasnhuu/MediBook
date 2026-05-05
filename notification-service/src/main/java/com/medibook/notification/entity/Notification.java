package com.medibook.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long userId;

    private String recipientEmail;

    private String title;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Boolean isRead = false;

    private Boolean emailSent = false;

    private LocalDateTime createdAt;
}