package com.medibook.appointment.service;

import com.medibook.appointment.entity.Appointment;
import com.medibook.appointment.entity.AppointmentStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment bookAppointment(Appointment appointment);
    Optional<Appointment> getAppointmentById(Long id);
    List<Appointment> getByPatientId(Long patientId);
    List<Appointment> getByProviderId(Long providerId);
    List<Appointment> getByDate(LocalDate date);
    List<Appointment> getByProviderAndDate(Long providerId, LocalDate date);
    Appointment updateStatus(Long id, AppointmentStatus status);
    Appointment cancelAppointment(Long id);
    Appointment completeAppointment(Long id);
    void deleteAppointment(Long id);
    List<Appointment> getAllAppointments();
}