package com.medibook.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "availability_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    private Long providerId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    private SlotStatus status;

    private String recurrence;

    private LocalDateTime createdAt;
}