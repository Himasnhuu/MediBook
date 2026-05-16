package com.medibook.schedule.service.impl;

import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.entity.SlotStatus;
import com.medibook.schedule.repository.SlotRepository;
import com.medibook.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private SlotRepository slotRepository;

    @Override
    public AvailabilitySlot addSlot(AvailabilitySlot slot) {
        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setCreatedAt(LocalDateTime.now());
        return slotRepository.save(slot);
    }

    @Override
    public List<AvailabilitySlot> addBulkSlots(
            List<AvailabilitySlot> slots) {
        slots.forEach(slot -> {
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setCreatedAt(LocalDateTime.now());
        });
        return slotRepository.saveAll(slots);
    }

    @Override
    public List<AvailabilitySlot> getSlotsByProvider(
            Long providerId) {
        return slotRepository.findByProviderId(providerId);
    }

    @Override
    public List<AvailabilitySlot> getAvailableSlots(
            Long providerId, LocalDate date) {
        return slotRepository
                .findByProviderIdAndDateAndStatus(
                        providerId, date, SlotStatus.AVAILABLE);
    }

    @Override
    public Optional<AvailabilitySlot> getSlotById(Long slotId) {
        return slotRepository.findById(slotId);
    }

    @Override
    public void bookSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException(
                        "Slot not found"));
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException(
                    "Slot is not available for booking");
        }
        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);
    }

    @Override
    public void releaseSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException(
                        "Slot not found"));
        slot.setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(slot);
    }

    @Override
    public void blockSlot(Long slotId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException(
                        "Slot not found"));
        slot.setStatus(SlotStatus.BLOCKED);
        slotRepository.save(slot);
    }

    @Override
    public AvailabilitySlot updateSlot(
            Long slotId, AvailabilitySlot updatedSlot) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException(
                        "Slot not found"));
        slot.setDate(updatedSlot.getDate());
        slot.setStartTime(updatedSlot.getStartTime());
        slot.setEndTime(updatedSlot.getEndTime());
        slot.setDurationMinutes(updatedSlot.getDurationMinutes());
        return slotRepository.save(slot);
    }

    @Override
    public void deleteSlot(Long slotId) {
        slotRepository.deleteById(slotId);
    }

    @Override
    public List<AvailabilitySlot> generateRecurringSlots(
            Long providerId,
            String recurrencePattern,
            LocalDate startDate,
            LocalDate endDate) {

        List<AvailabilitySlot> generatedSlots = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setProviderId(providerId);
            slot.setDate(current);
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setRecurrence(recurrencePattern);
            slot.setCreatedAt(LocalDateTime.now());
            generatedSlots.add(slot);

            if (recurrencePattern.equalsIgnoreCase("DAILY")) {
                current = current.plusDays(1);
            } else if (recurrencePattern
                    .equalsIgnoreCase("WEEKLY")) {
                current = current.plusWeeks(1);
            } else {
                break;
            }
        }
        return slotRepository.saveAll(generatedSlots);
    }
}