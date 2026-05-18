package com.medibook.schedule.controller;

import com.medibook.schedule.entity.AvailabilitySlot;
import com.medibook.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/slots")
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;

	@PostMapping
	@PreAuthorize("hasRole('PROVIDER')")
	public ResponseEntity<AvailabilitySlot> addSlot(@RequestBody AvailabilitySlot slot) {
		return ResponseEntity.ok(scheduleService.addSlot(slot));
	}

	@PostMapping("/bulk")
	@PreAuthorize("hasRole('PROVIDER')")
	public ResponseEntity<List<AvailabilitySlot>> addBulkSlots(@RequestBody List<AvailabilitySlot> slots) {
		return ResponseEntity.ok(scheduleService.addBulkSlots(slots));
	}

	@GetMapping("/provider/{providerId}")
	public ResponseEntity<List<AvailabilitySlot>> getByProvider(@PathVariable Long providerId) {
		return ResponseEntity.ok(scheduleService.getSlotsByProvider(providerId));
	}

	@GetMapping("/available")
	public ResponseEntity<List<AvailabilitySlot>> getAvailable(@RequestParam Long providerId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ResponseEntity.ok(scheduleService.getAvailableSlots(providerId, date));
	}

	@GetMapping("/{slotId}")
	public ResponseEntity<AvailabilitySlot> getById(@PathVariable Long slotId) {
		return ResponseEntity
				.ok(scheduleService.getSlotById(slotId).orElseThrow(() -> new RuntimeException("Slot not found")));
	}

	@PutMapping("/{slotId}/book")
	@PreAuthorize("hasRole('PATIENT') or hasRole('PROVIDER')")
	public ResponseEntity<String> bookSlot(@PathVariable Long slotId) {
		scheduleService.bookSlot(slotId);
		return ResponseEntity.ok("Slot booked successfully");
	}

	@PutMapping("/{slotId}/release")
	@PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN') or hasRole('PATIENT')")
	public ResponseEntity<String> releaseSlot(@PathVariable Long slotId) {
		scheduleService.releaseSlot(slotId);
		return ResponseEntity.ok("Slot released successfully");
	}

	@PutMapping("/{slotId}/block")
	@PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
	public ResponseEntity<String> blockSlot(@PathVariable Long slotId) {
		scheduleService.blockSlot(slotId);
		return ResponseEntity.ok("Slot blocked successfully");
	}

	@PutMapping("/{slotId}")
	@PreAuthorize("hasRole('PROVIDER')")
	public ResponseEntity<AvailabilitySlot> updateSlot(@PathVariable Long slotId, @RequestBody AvailabilitySlot slot) {
		return ResponseEntity.ok(scheduleService.updateSlot(slotId, slot));
	}

	@DeleteMapping("/{slotId}")
	@PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
	public ResponseEntity<String> deleteSlot(@PathVariable Long slotId) {
		scheduleService.deleteSlot(slotId);
		return ResponseEntity.ok("Slot deleted successfully");
	}

	@PostMapping("/recurring")
	@PreAuthorize("hasRole('PROVIDER')")
	public ResponseEntity<List<AvailabilitySlot>> generateRecurring(@RequestParam Long providerId,
			@RequestParam String recurrencePattern,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return ResponseEntity
				.ok(scheduleService.generateRecurringSlots(providerId, recurrencePattern, startDate, endDate));
	}
}