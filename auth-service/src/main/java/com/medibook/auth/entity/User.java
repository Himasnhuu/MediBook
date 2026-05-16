package com.medibook.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    private String phone;

    private String role; // PATIENT / PROVIDER / ADMIN

    private String provider;

    private boolean isActive;

    private LocalDateTime createdAt;

    private String profilePicUrl;
}