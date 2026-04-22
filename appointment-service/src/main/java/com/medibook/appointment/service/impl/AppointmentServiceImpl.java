package com.medibook.appointment.service.impl;

import com.medibook.appointment.entity.Appointment;
import com.medibook.appointment.entity.AppointmentStatus;
import com.medibook.appointment.repository.AppointmentRepository;
import com.medibook.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public Appointment bookAppointment(Appointment appointment) {
        // Check if slot is already booked
        boolean slotTaken = appointmentRepository.existsBySlotIdAndStatusNot(
            appointment.getSlotId(), AppointmentStatus.CANCELLED
        );
        if (slotTaken) {
            throw new RuntimeException("Slot is already booked");
        }
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    @Override
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public List<Appointment> getByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getByProviderId(Long providerId) {
        return appointmentRepository.findByProviderId(providerId);
    }

    @Override
    public List<Appointment> getByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }

    @Override
    public List<Appointment> getByProviderAndDate(Long providerId, LocalDate date) {
        return appointmentRepository.findByProviderIdAndAppointmentDate(providerId, date);
    }

    @Override
    public Appointment updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment cancelAppointment(Long id) {
        return updateStatus(id, AppointmentStatus.CANCELLED);
    }

    @Override
    public Appointment completeAppointment(Long id) {
        return updateStatus(id, AppointmentStatus.COMPLETED);
    }

    @Override
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointmentRepository.delete(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}