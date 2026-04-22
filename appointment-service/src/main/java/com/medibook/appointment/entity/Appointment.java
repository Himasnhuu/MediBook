package com.medibook.appointment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    private Long patientId;

    private Long providerId;

    private Long slotId;

    private String serviceType;

    private LocalDate appointmentDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String notes;

    private String modeOfConsultation; // IN_PERSON / TELECONSULTATION

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}