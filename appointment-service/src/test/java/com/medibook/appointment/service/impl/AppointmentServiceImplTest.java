package com.medibook.appointment.service.impl;

import com.medibook.appointment.entity.Appointment;
import com.medibook.appointment.entity.AppointmentStatus;
import com.medibook.appointment.event.AppointmentEventPublisher;
import com.medibook.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setPatientId(10L);
        appointment.setProviderId(5L);
        appointment.setSlotId(20L);
        appointment.setServiceType("Fever");
        appointment.setAppointmentDate(LocalDate.of(2026, 6, 1));
        appointment.setStartTime(LocalTime.of(10, 0));
        appointment.setEndTime(LocalTime.of(10, 30));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setModeOfConsultation("IN_PERSON");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
    }

    // ── bookAppointment ──

    @Test
    void bookAppointment_Success() {
        when(appointmentRepository.existsBySlotIdAndStatusNot(
            eq(20L), eq(AppointmentStatus.CANCELLED))).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.bookAppointment(appointment);

        assertNotNull(result);
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        assertEquals(1L, result.getAppointmentId());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_SlotAlreadyTaken_ThrowsException() {
        when(appointmentRepository.existsBySlotIdAndStatusNot(
            eq(20L), eq(AppointmentStatus.CANCELLED))).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> appointmentService.bookAppointment(appointment));

        assertEquals("Slot is already booked", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    // ── getAppointmentById ──

    @Test
    void getAppointmentById_Found() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        Optional<Appointment> result = appointmentService.getAppointmentById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getAppointmentId());
    }

    @Test
    void getAppointmentById_NotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Appointment> result = appointmentService.getAppointmentById(99L);

        assertFalse(result.isPresent());
    }

    // ── cancelAppointment ──

    @Test
    void cancelAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.cancelAppointment(1L);

        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void cancelAppointment_NotFound_ThrowsException() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> appointmentService.cancelAppointment(99L));
    }

    // ── completeAppointment ──

    @Test
    void completeAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.completeAppointment(1L);

        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    // ── getByPatientId ──

    @Test
    void getByPatientId_ReturnsList() {
        when(appointmentRepository.findByPatientId(10L))
            .thenReturn(Arrays.asList(appointment));

        List<Appointment> results = appointmentService.getByPatientId(10L);

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).getPatientId());
    }

    @Test
    void getByPatientId_ReturnsEmptyList() {
        when(appointmentRepository.findByPatientId(99L))
            .thenReturn(Arrays.asList());

        List<Appointment> results = appointmentService.getByPatientId(99L);

        assertTrue(results.isEmpty());
    }

    // ── getByProviderId ──

    @Test
    void getByProviderId_ReturnsList() {
        when(appointmentRepository.findByProviderId(5L))
            .thenReturn(Arrays.asList(appointment));

        List<Appointment> results = appointmentService.getByProviderId(5L);

        assertEquals(1, results.size());
        assertEquals(5L, results.get(0).getProviderId());
    }

    // ── deleteAppointment ──

    @Test
    void deleteAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        doNothing().when(appointmentRepository).delete(appointment);

        assertDoesNotThrow(() -> appointmentService.deleteAppointment(1L));
        verify(appointmentRepository, times(1)).delete(appointment);
    }

    @Test
    void deleteAppointment_NotFound_ThrowsException() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> appointmentService.deleteAppointment(99L));
    }

    // ── updateStatus ──

    @Test
    void updateStatus_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.updateStatus(1L, AppointmentStatus.COMPLETED);

        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());
    }

    // ── getCountByProviderId ──

    @Test
    void getCountByProviderId_ReturnsCount() {
        when(appointmentRepository.countByProviderId(5L)).thenReturn(3L);

        long count = appointmentService.getCountByProviderId(5L);

        assertEquals(3L, count);
    }

    // ── getAllAppointments ──

    @Test
    void getAllAppointments_ReturnsList() {
        when(appointmentRepository.findAll())
            .thenReturn(Arrays.asList(appointment));

        List<Appointment> results = appointmentService.getAllAppointments();

        assertEquals(1, results.size());
    }
}