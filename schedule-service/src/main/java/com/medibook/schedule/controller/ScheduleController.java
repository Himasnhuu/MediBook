package com.medibook.schedule.controller;

import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/slots")
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;

	// Add a single slot
	@PostMapping
	public ResponseEntity<AvailabilitySlot> addSlot(@RequestBody AvailabilitySlot slot) {
		return ResponseEntity.ok(scheduleService.addSlot(slot));
	}

	// Add multiple slots at once
	@PostMapping("/bulk")
	public ResponseEntity<List<AvailabilitySlot>> addBulkSlots(@RequestBody List<AvailabilitySlot> slots) {
		return ResponseEntity.ok(scheduleService.addBulkSlots(slots));
	}

	// Get all slots for a provider
	@GetMapping("/provider/{providerId}")
	public ResponseEntity<List<AvailabilitySlot>> getByProvider(@PathVariable Long providerId) {
		return ResponseEntity.ok(scheduleService.getSlotsByProvider(providerId));
	}

	// Get available slots for a provider on a date
	@GetMapping("/available")
	public ResponseEntity<List<AvailabilitySlot>> getAvailable(@RequestParam Long providerId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(scheduleService.getAvailableSlots(providerId, date));
	}

	// Get a single slot by ID
	@GetMapping("/{slotId}")
	public ResponseEntity<AvailabilitySlot> getById(@PathVariable Long slotId) {
		return ResponseEntity
				.ok(scheduleService.getSlotById(slotId).orElseThrow(() -> new RuntimeException("Slot not found")));
	}

	// Book a slot
	@PutMapping("/{slotId}/book")
	public ResponseEntity<String> bookSlot(@PathVariable Long slotId) {
		scheduleService.bookSlot(slotId);
		return ResponseEntity.ok("Slot booked successfully");
	}

	// Release a slot (on cancellation)
	@PutMapping("/{slotId}/release")
	public ResponseEntity<String> releaseSlot(@PathVariable Long slotId) {
		scheduleService.releaseSlot(slotId);
		return ResponseEntity.ok("Slot released successfully");
	}

	// Block a slot
	@PutMapping("/{slotId}/block")
	public ResponseEntity<String> blockSlot(@PathVariable Long slotId) {
		scheduleService.blockSlot(slotId);
		return ResponseEntity.ok("Slot blocked successfully");
	}

	// Update a slot
	@PutMapping("/{slotId}")
	public ResponseEntity<AvailabilitySlot> updateSlot(@PathVariable Long slotId, @RequestBody AvailabilitySlot slot) {
		return ResponseEntity.ok(scheduleService.updateSlot(slotId, slot));
	}

	// Delete a slot
	@DeleteMapping("/{slotId}")
	public ResponseEntity<String> deleteSlot(@PathVariable Long slotId) {
		scheduleService.deleteSlot(slotId);
		return ResponseEntity.ok("Slot deleted successfully");
	}

	// Generate recurring slots
	@PostMapping("/recurring")
	public ResponseEntity<List<AvailabilitySlot>> generateRecurring(@RequestParam Long providerId,
			@RequestParam String recurrencePattern,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return ResponseEntity
				.ok(scheduleService.generateRecurringSlots(providerId, recurrencePattern, startDate, endDate));
	}
}