package com.medibook.schedule.service;

import com.medibook.schedule.entity.AvailabilitySlot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    // Add a single slot
    AvailabilitySlot addSlot(AvailabilitySlot slot);

    // Add multiple slots at once
    List<AvailabilitySlot> addBulkSlots(
            List<AvailabilitySlot> slots);

    // Get all slots for a provider
    List<AvailabilitySlot> getSlotsByProvider(Long providerId);

    // Get available slots for a provider on a date
    List<AvailabilitySlot> getAvailableSlots(
            Long providerId, LocalDate date);

    // Get a single slot by ID
    Optional<AvailabilitySlot> getSlotById(Long slotId);

    // Mark slot as BOOKED
    void bookSlot(Long slotId);

    // Mark slot as AVAILABLE again (on cancellation)
    void releaseSlot(Long slotId);

    // Mark slot as BLOCKED (doctor unavailable)
    void blockSlot(Long slotId);

    // Update slot details
    AvailabilitySlot updateSlot(
            Long slotId, AvailabilitySlot slot);

    // Delete a slot
    void deleteSlot(Long slotId);

    // Generate recurring slots
    List<AvailabilitySlot> generateRecurringSlots(
            Long providerId,
            String recurrencePattern,
            LocalDate startDate,
            LocalDate endDate);
}