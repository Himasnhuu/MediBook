package com.medibook.schedule.repository;

import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SlotRepository 
        extends JpaRepository<AvailabilitySlot, Long> {

    // Get all slots for a provider
    List<AvailabilitySlot> findByProviderId(Long providerId);

    // Get slots for a provider on a specific date
    List<AvailabilitySlot> findByProviderIdAndDate(
            Long providerId, LocalDate date);

    // Get only AVAILABLE slots for a provider on a date
    List<AvailabilitySlot> findByProviderIdAndDateAndStatus(
            Long providerId, LocalDate date, SlotStatus status);

    // Get slots between two dates
    List<AvailabilitySlot> findByProviderIdAndDateBetween(
            Long providerId, LocalDate start, LocalDate end);

    // Count available slots for a provider
    long countByProviderIdAndStatus(
            Long providerId, SlotStatus status);

    // Get all slots by status
    List<AvailabilitySlot> findByStatus(SlotStatus status);

    // Delete all slots for a provider
    void deleteByProviderId(Long providerId);
}